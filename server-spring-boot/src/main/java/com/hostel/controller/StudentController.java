package com.hostel.controller;

import com.hostel.dto.RoomChangeRequestDto;
import com.hostel.model.RoomChangeRequest;
import com.hostel.model.Bed;
import com.hostel.model.Room;
import com.hostel.model.User;
import com.hostel.repository.RoomChangeRequestRepository;
import com.hostel.repository.BedRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentController {
    
    @Autowired
    private RoomChangeRequestRepository roomChangeRequestRepository;
    
    @Autowired
    private BedRepository bedRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/room-change-request")
    public ResponseEntity<?> submitRoomChangeRequest(
            @Valid @RequestBody RoomChangeRequestDto requestDto, 
            HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.status(401)
                    .body(Map.of("error", "Unauthorized"));
            }
            
            // Get current room of student
            Optional<Bed> currentBedOptional = bedRepository.findByStudentId(userId);
            String currentRoomId = currentBedOptional.map(Bed::getRoomId).orElse(null);
            
            RoomChangeRequest roomChangeRequest = new RoomChangeRequest(
                userId,
                currentRoomId,
                requestDto.getRequestedRoomId(),
                requestDto.getReason()
            );
            
            roomChangeRequestRepository.save(roomChangeRequest);
            
            return ResponseEntity.ok(Map.of("message", "Room change request submitted successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to submit request"));
        }
    }
    
    @GetMapping("/my-room")
    public ResponseEntity<?> getMyRoom(HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.status(401)
                    .body(Map.of("error", "Unauthorized"));
            }
            
            Optional<Bed> myBedOptional = bedRepository.findByStudentId(userId);
            
            if (myBedOptional.isEmpty()) {
                return ResponseEntity.status(404)
                    .body(Map.of("error", "No room assigned"));
            }
            
            Bed myBed = myBedOptional.get();
            Optional<Room> roomOptional = roomRepository.findById(myBed.getRoomId());
            
            if (roomOptional.isEmpty()) {
                return ResponseEntity.status(404)
                    .body(Map.of("error", "Room not found"));
            }
            
            Room room = roomOptional.get();
            
            // Get roommates
            List<Bed> roomBeds = bedRepository.findByRoomId(myBed.getRoomId());
            List<Map<String, Object>> roommates = roomBeds.stream()
                .filter(bed -> bed.getStudentId() != null && !bed.getStudentId().equals(userId))
                .map(bed -> {
                    User student = userRepository.findById(bed.getStudentId()).orElse(null);
                    return Map.of("full_name", student != null ? student.getFullName() : "Unknown");
                })
                .collect(Collectors.toList());
            
            Map<String, Object> myRoomDetails = Map.of(
                "id", room.getId(),
                "room_number", room.getRoomNumber(),
                "floor", room.getFloor(),
                "capacity", room.getCapacity(),
                "room_type", room.getRoomType(),
                "bed_number", myBed.getBedNumber(),
                "roommates", roommates
            );
            
            return ResponseEntity.ok(myRoomDetails);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Database error"));
        }
    }
} 