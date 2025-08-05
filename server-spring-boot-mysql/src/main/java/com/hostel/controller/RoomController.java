package com.hostel.controller;

import com.hostel.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Room Management", description = "Endpoints for room information and availability management")
public class RoomController {
    
    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }
    
    @GetMapping("/rooms")
    @Operation(
        summary = "Get All Rooms", 
        description = "Retrieve all rooms with their occupancy statistics and availability status"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rooms retrieved successfully",
            content = @Content(schema = @Schema(example = "[{\"roomNumber\": \"R001\", \"capacity\": 4, \"occupied\": 2, \"available\": 2}]"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(example = "{\"error\": \"Database error\"}")))
    })
    public ResponseEntity<?> getAllRooms() {
        try {
            List<Map<String, Object>> roomsWithStats = roomService.getAllRoomsWithStats();
            return ResponseEntity.ok(roomsWithStats);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Database error"));
        }
    }
    
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<?> getRoomDetails(@PathVariable Long roomId) {
        try {
            Map<String, Object> roomDetails = roomService.getRoomDetails(roomId);
            return ResponseEntity.ok(roomDetails);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Database error"));
        }
    }
    
    // Debug endpoint to check database state
    @GetMapping("/debug/rooms")
    public ResponseEntity<?> debugRooms() {
        try {
            Map<String, Object> debugInfo = roomService.getDebugInfo();
            return ResponseEntity.ok(debugInfo);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Debug error: " + e.getMessage()));
        }
    }
    
    // Endpoint to force reinitialize data (for testing only)
    @PostMapping("/debug/reinitialize")
    public ResponseEntity<?> forceReinitialize() {
        try {
            // This would need to be injected - let's return a message for now
            return ResponseEntity.ok(Map.of("message", "Use the debug endpoint to check current state, then restart the server to reinitialize"));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Reinitialize error: " + e.getMessage()));
        }
    }
}