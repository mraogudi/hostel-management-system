package com.hostel.controller;

import com.hostel.dto.CreateStudentRequest;
import com.hostel.dto.AssignRoomRequest;
import com.hostel.model.User;
import com.hostel.model.Bed;
import com.hostel.model.Room;
import com.hostel.model.RoomChangeRequest;
import com.hostel.repository.UserRepository;
import com.hostel.repository.BedRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.repository.RoomChangeRequestRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warden")
@CrossOrigin(origins = "*")
public class WardenController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BedRepository bedRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private RoomChangeRequestRepository roomChangeRequestRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();
    
    private String generateRandomPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
    
    @PostMapping("/create-student")
    public ResponseEntity<?> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        try {
            // Check if username already exists
            if (userRepository.existsByUsername(request.getUsername())) {
                return ResponseEntity.status(400)
                    .body(Map.of("error", "Username already exists"));
            }
            
            String password = generateRandomPassword(8);
            String hashedPassword = passwordEncoder.encode(password);
            
            User newStudent = new User(
                request.getUsername(),
                hashedPassword,
                "student",
                request.getFullName(),
                request.getEmail(),
                request.getPhone()
            );
            
            User savedStudent = userRepository.save(newStudent);
            
            return ResponseEntity.ok(Map.of(
                "message", "Student created successfully",
                "credentials", Map.of(
                    "username", request.getUsername(),
                    "password", password
                ),
                "student_id", savedStudent.getId()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to create student"));
        }
    }
    
    @PostMapping("/assign-room")
    public ResponseEntity<?> assignRoom(@Valid @RequestBody AssignRoomRequest request) {
        try {
            Optional<Bed> bedOptional = bedRepository.findByRoomIdAndBedNumber(
                request.getRoomId(), request.getBedNumber());
            
            if (bedOptional.isEmpty() || !"available".equals(bedOptional.get().getStatus())) {
                return ResponseEntity.status(400)
                    .body(Map.of("error", "Bed not available"));
            }
            
            Bed bed = bedOptional.get();
            bed.setStudentId(request.getStudentId());
            bed.setStatus("occupied");
            
            bedRepository.save(bed);
            
            // Update room occupied beds count
            Room room = roomRepository.findById(request.getRoomId()).orElse(null);
            if (room != null) {
                long occupiedCount = bedRepository.countByRoomIdAndStatus(request.getRoomId(), "occupied");
                room.setOccupiedBeds((int) occupiedCount);
                roomRepository.save(room);
            }
            
            return ResponseEntity.ok(Map.of("message", "Room assigned successfully"));
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to assign room"));
        }
    }
    
    @GetMapping("/room-change-requests")
    public ResponseEntity<?> getRoomChangeRequests() {
        try {
            List<RoomChangeRequest> requests = roomChangeRequestRepository.findAllByOrderByRequestedAtDesc();
            
            List<Map<String, Object>> requestsWithDetails = requests.stream().map(request -> {
                User student = userRepository.findById(request.getStudentId()).orElse(null);
                Room currentRoom = request.getCurrentRoomId() != null ? 
                    roomRepository.findById(request.getCurrentRoomId()).orElse(null) : null;
                Room requestedRoom = roomRepository.findById(request.getRequestedRoomId()).orElse(null);
                
                return Map.of(
                    "id", request.getId(),
                    "student_id", request.getStudentId(),
                    "student_name", student != null ? student.getFullName() : "Unknown",
                    "current_room_id", request.getCurrentRoomId() != null ? request.getCurrentRoomId() : "",
                    "current_room", currentRoom != null ? currentRoom.getRoomNumber() : null,
                    "requested_room_id", request.getRequestedRoomId(),
                    "requested_room", requestedRoom != null ? requestedRoom.getRoomNumber() : "Unknown",
                    "reason", request.getReason(),
                    "status", request.getStatus(),
                    "requested_at", request.getRequestedAt(),
                    "processed_at", request.getProcessedAt(),
                    "processed_by", request.getProcessedBy()
                );
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(requestsWithDetails);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Database error"));
        }
    }
} 