package com.hostel.controller;

import com.hostel.dto.CreateStudentRequest;
import com.hostel.dto.AssignRoomRequest;
import com.hostel.model.RoomChangeRequest;
import com.hostel.model.PersonalDetailsUpdateRequest;
import com.hostel.model.User;
import com.hostel.service.StudentService;
import com.hostel.repository.UserRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.model.Room;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Warden Management", description = "Endpoints for warden operations - student management, room assignments, and request approvals")
@SecurityRequirement(name = "Bearer Authentication")
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
    @Operation(
        summary = "Create New Student", 
        description = "Create a new student account with personal details and room assignment"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Student created successfully",
            content = @Content(schema = @Schema(example = "{\"message\": \"Student created successfully\", \"studentId\": \"123\"}"))),
        @ApiResponse(responseCode = "400", description = "Invalid request data",
            content = @Content(schema = @Schema(example = "{\"error\": \"Username already exists\"}"))),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Warden access required",
            content = @Content(schema = @Schema(example = "{\"error\": \"Unauthorized\"}"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(example = "{\"error\": \"Database error\"}")))
    })
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

    @GetMapping("/students")
    public ResponseEntity<?> getAllStudents() {
        try {
            List<Map<String, Object>> students = studentService.getAllStudents();
            return ResponseEntity.ok(students);
            
        } catch (Exception e) {
            System.err.println("Error fetching students: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to fetch students: " + e.getMessage()));
        }
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        try {
            Map<String, Object> student = studentService.getStudentById(id);
            return ResponseEntity.ok(student);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to fetch student"));
        }
    }

    @PutMapping("/students/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Map<String, Object> studentData) {
        try {
            Map<String, Object> updatedStudent = studentService.updateStudent(id, studentData);
            return ResponseEntity.ok(updatedStudent);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to update student"));
        }
    }

    @DeleteMapping("/students/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        try {
            studentService.deleteStudent(id);
            return ResponseEntity.ok(Map.of("message", "Student deleted successfully"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to delete student"));
        }
    }

    @PutMapping("/room-change-requests/{requestId}/approve")
    public ResponseEntity<?> approveRoomChangeRequest(@PathVariable Long requestId) {
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
    public ResponseEntity<?> rejectRoomChangeRequest(@PathVariable Long requestId) {
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

    @GetMapping("/personal-details-update-requests")
    public ResponseEntity<?> getPersonalDetailsUpdateRequests() {
        try {
            List<PersonalDetailsUpdateRequest> requests = studentService.getAllPersonalDetailsUpdateRequests();
            
            List<Map<String, Object>> requestsWithDetails = requests.stream().map(request -> {
                Map<String, Object> requestMap = new HashMap<>();
                requestMap.put("id", request.getId());
                requestMap.put("student_id", request.getStudentId());
                requestMap.put("student_name", request.getStudentName());
                requestMap.put("roll_no", request.getStudentRollNo());
                requestMap.put("phone", request.getPhone());
                requestMap.put("address_line1", request.getAddressLine1());
                requestMap.put("address_line2", request.getAddressLine2());
                requestMap.put("city", request.getCity());
                requestMap.put("state", request.getState());
                requestMap.put("postal_code", request.getPostalCode());
                requestMap.put("guardian_name", request.getGuardianName());
                requestMap.put("guardian_phone", request.getGuardianPhone());
                requestMap.put("guardian_address", request.getGuardianAddress());
                requestMap.put("status", request.getStatus());
                requestMap.put("requested_at", request.getCreatedAt());
                requestMap.put("processed_at", request.getProcessedAt());
                requestMap.put("processed_by", request.getProcessedBy());
                requestMap.put("warden_comments", request.getWardenComments());
                return requestMap;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(requestsWithDetails);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to fetch personal details update requests"));
        }
    }

    @PutMapping("/personal-details-update-requests/{requestId}/approve")
    public ResponseEntity<?> approvePersonalDetailsUpdateRequest(
            @PathVariable Long requestId, 
            @RequestBody Map<String, String> requestBody) {
        try {
            String wardenComments = requestBody.get("comments");
            studentService.approvePersonalDetailsUpdateRequest(requestId, wardenComments);
            return ResponseEntity.ok(Map.of("message", "Personal details update request approved successfully"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to approve personal details update request"));
        }
    }

    @PutMapping("/personal-details-update-requests/{requestId}/reject")
    public ResponseEntity<?> rejectPersonalDetailsUpdateRequest(
            @PathVariable Long requestId, 
            @RequestBody Map<String, String> requestBody) {
        try {
            String wardenComments = requestBody.get("comments");
            studentService.rejectPersonalDetailsUpdateRequest(requestId, wardenComments);
            return ResponseEntity.ok(Map.of("message", "Personal details update request rejected successfully"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to reject personal details update request"));
        }
    }
} 