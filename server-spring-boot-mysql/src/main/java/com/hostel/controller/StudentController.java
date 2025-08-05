package com.hostel.controller;

import com.hostel.dto.RoomChangeRequestDto;
import com.hostel.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/student")
@CrossOrigin(origins = "*")
public class StudentController {
    
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/room-change-request")
    public ResponseEntity<?> submitRoomChangeRequest(
            @Valid @RequestBody RoomChangeRequestDto requestDto, 
            HttpServletRequest request) {
        try {
            String userIdStr = (String) request.getAttribute("userId");
            
            if (userIdStr == null) {
                return ResponseEntity.status(401)
                    .body(Map.of("error", "Unauthorized"));
            }
            
            Long userId = Long.parseLong(userIdStr);
            studentService.submitRoomChangeRequest(userId, requestDto);
            return ResponseEntity.ok(Map.of("message", "Room change request submitted successfully"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to submit request"));
        }
    }
    
    @GetMapping("/my-room")
    public ResponseEntity<?> getMyRoom(HttpServletRequest request) {
        try {
            String userIdStr = (String) request.getAttribute("userId");
            
            if (userIdStr == null) {
                return ResponseEntity.status(401)
                    .body(Map.of("error", "Unauthorized"));
            }
            
            Long userId = Long.parseLong(userIdStr);
            Map<String, Object> myRoomDetails = studentService.getStudentRoom(userId);
            return ResponseEntity.ok(myRoomDetails);
            
        } catch (RuntimeException e) {
            if (e.getMessage().equals("No room assigned")) {
                return ResponseEntity.status(404)
                    .body(Map.of("error", "No room assigned"));
            } else if (e.getMessage().equals("Room not found")) {
                return ResponseEntity.status(404)
                    .body(Map.of("error", "Room not found"));
            }
            return ResponseEntity.status(400)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Database error"));
        }
    }
    
    @GetMapping("/warden-contact")
    public ResponseEntity<?> getWardenContact() {
        try {
            Map<String, Object> wardenContact = studentService.getWardenContact();
            return ResponseEntity.ok(wardenContact);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to get warden contact information"));
        }
    }
    
    @PostMapping("/personal-details-update-request")
    public ResponseEntity<?> submitPersonalDetailsUpdateRequest(
            @RequestBody Map<String, String> updateRequest, 
            HttpServletRequest request) {
        try {
            String userIdStr = (String) request.getAttribute("userId");
            
            if (userIdStr == null) {
                return ResponseEntity.status(401)
                    .body(Map.of("error", "Unauthorized"));
            }
            
            Long userId = Long.parseLong(userIdStr);
            Map<String, Object> response = studentService.submitPersonalDetailsUpdateRequest(userId, updateRequest);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to submit personal details update request"));
        }
    }
} 