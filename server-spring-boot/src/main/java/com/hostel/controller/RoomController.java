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
                
                return Map.of(
                    "id", room.getId(),
                    "room_number", room.getRoomNumber(),
                    "floor", room.getFloor(),
                    "capacity", room.getCapacity(),
                    "room_type", room.getRoomType(),
                    "occupied_beds", occupiedBeds,
                    "available_beds", availableBeds,
                    "created_at", room.getCreatedAt()
                );
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
                
                return Map.of(
                    "id", bed.getId(),
                    "bed_number", bed.getBedNumber(),
                    "status", bed.getStatus(),
                    "student_id", bed.getStudentId() != null ? bed.getStudentId() : "",
                    "student_name", student != null ? student.getFullName() : null
                );
            }).collect(Collectors.toList());
            
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