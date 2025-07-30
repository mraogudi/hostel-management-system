package com.hostel.controller;

import com.hostel.dto.CreateStudentRequest;
import com.hostel.dto.AssignRoomRequest;
import com.hostel.model.RoomChangeRequest;
import com.hostel.model.User;
import com.hostel.service.StudentService;
import com.hostel.repository.UserRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.model.Room;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warden")
@CrossOrigin(origins = "*")
public class WardenController {

    private final StudentService studentService;
    
    private final UserRepository userRepository;

    private final RoomRepository roomRepository;

    public WardenController(StudentService studentService, UserRepository userRepository, RoomRepository roomRepository) {
        this.studentService = studentService;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
    }

    @PostMapping("/create-student")
    public ResponseEntity<?> createStudent(@Valid @RequestBody CreateStudentRequest request) {
        try {
            Map<String, Object> response = studentService.createStudent(request);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to create student: " + e.getMessage()));
        }
    }
    
    @PostMapping("/assign-room")
    public ResponseEntity<?> assignRoom(@Valid @RequestBody AssignRoomRequest request) {
        try {
            // Note: request.getStudentId() actually contains the roll number
            studentService.assignRoom(request.getStudentId(), request.getRoomId(), request.getBedNumber());
            return ResponseEntity.ok(Map.of("message", "Room assigned successfully"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to assign room"));
        }
    }
    
    @GetMapping("/room-change-requests")
    public ResponseEntity<?> getRoomChangeRequests() {
        try {
            List<RoomChangeRequest> requests = studentService.getAllRoomChangeRequests();
            
            List<Map<String, Object>> requestsWithDetails = requests.stream().map(request -> {
                User student = userRepository.findById(request.getStudentId()).orElse(null);
                Room currentRoom = request.getCurrentRoomId() != null ? 
                    roomRepository.findById(request.getCurrentRoomId()).orElse(null) : null;
                Room requestedRoom = roomRepository.findById(request.getRequestedRoomId()).orElse(null);
                
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("id", request.getId());
                requestMap.put("student_id", request.getStudentId());
                requestMap.put("student_name", student != null ? student.getFullName() : "Unknown");
                requestMap.put("current_room_id", request.getCurrentRoomId() != null ? request.getCurrentRoomId() : "");
                requestMap.put("current_room", currentRoom != null ? currentRoom.getRoomNumber() : null);
                requestMap.put("requested_room_id", request.getRequestedRoomId());
                requestMap.put("requested_room", requestedRoom != null ? requestedRoom.getRoomNumber() : "Unknown");
                requestMap.put("requested_bed_number", request.getRequestedBedNumber());
                requestMap.put("reason", request.getReason());
                requestMap.put("status", request.getStatus());
                requestMap.put("requested_at", request.getRequestedAt());
                requestMap.put("processed_at", request.getProcessedAt());
                requestMap.put("processed_by", request.getProcessedBy());
                return requestMap;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(requestsWithDetails);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Database error"));
        }
    }

    @PutMapping("/room-change-requests/{requestId}/approve")
    public ResponseEntity<?> approveRoomChangeRequest(@PathVariable String requestId) {
        try {
            studentService.approveRoomChangeRequest(requestId);
            return ResponseEntity.ok(Map.of("message", "Room change request approved successfully"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to approve room change request"));
        }
    }

    @PutMapping("/room-change-requests/{requestId}/reject")
    public ResponseEntity<?> rejectRoomChangeRequest(@PathVariable String requestId) {
        try {
            studentService.rejectRoomChangeRequest(requestId);
            return ResponseEntity.ok(Map.of("message", "Room change request rejected successfully"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to reject room change request"));
        }
    }
} 