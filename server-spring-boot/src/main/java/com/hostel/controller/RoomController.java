package com.hostel.controller;

import com.hostel.model.Room;
import com.hostel.model.Bed;
import com.hostel.model.User;
import com.hostel.repository.RoomRepository;
import com.hostel.repository.BedRepository;
import com.hostel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RoomController {
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private BedRepository bedRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/rooms")
    public ResponseEntity<?> getAllRooms() {
        try {
            List<Room> rooms = roomRepository.findAll();
            
            List<Map<String, Object>> roomsWithStats = rooms.stream().map(room -> {
                long occupiedBeds = bedRepository.countByRoomIdAndStatus(room.getId(), "occupied");
                long availableBeds = bedRepository.countByRoomIdAndStatus(room.getId(), "available");
                
                Map<String, Object> roomMap = new HashMap<>();
                roomMap.put("id", room.getId());
                roomMap.put("room_number", room.getRoomNumber());
                roomMap.put("floor", room.getFloor());
                roomMap.put("capacity", room.getCapacity());
                roomMap.put("room_type", room.getRoomType());
                roomMap.put("occupied_beds", occupiedBeds);
                roomMap.put("available_beds", availableBeds);
                roomMap.put("created_at", room.getCreatedAt());
                return roomMap;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(roomsWithStats);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Database error"));
        }
    }
    
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<?> getRoomDetails(@PathVariable String roomId) {
        try {
            Optional<Room> roomOptional = roomRepository.findById(roomId);
            
            if (roomOptional.isEmpty()) {
                return ResponseEntity.status(404)
                    .body(Map.of("error", "Room not found"));
            }
            
            Room room = roomOptional.get();
            List<Bed> beds = bedRepository.findByRoomId(roomId);
            
            List<Map<String, Object>> bedsWithStudents = beds.stream().map(bed -> {
                User student = bed.getStudentId() != null ? 
                    userRepository.findById(bed.getStudentId()).orElse(null) : null;
                
                Map<String, Object> bedMap = new HashMap<>();
                bedMap.put("id", bed.getId());
                bedMap.put("bed_number", bed.getBedNumber());
                bedMap.put("status", bed.getStatus());
                bedMap.put("student_id", bed.getStudentId() != null ? bed.getStudentId() : "");
                bedMap.put("student_name", student != null ? student.getFullName() : null);
                return bedMap;
            }).toList();
            
            Map<String, Object> roomDetails = Map.of(
                "id", room.getId(),
                "room_number", room.getRoomNumber(),
                "floor", room.getFloor(),
                "capacity", room.getCapacity(),
                "room_type", room.getRoomType(),
                "created_at", room.getCreatedAt(),
                "beds", bedsWithStudents
            );
            
            return ResponseEntity.ok(roomDetails);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Database error"));
        }
    }
} 