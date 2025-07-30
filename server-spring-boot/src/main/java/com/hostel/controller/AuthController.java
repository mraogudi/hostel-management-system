package com.hostel.controller;

import com.hostel.dto.LoginRequest;
import com.hostel.dto.LoginResponse;
import com.hostel.dto.UserDto;
import com.hostel.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(401)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Database error"));
        }
    }
    
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.status(401)
                    .body(Map.of("error", "Unauthorized"));
            }
            
            UserDto userDto = authService.getUserProfile(userId);
            return ResponseEntity.ok(userDto);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(404)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Database error"));
        }
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, HttpServletRequest httpRequest) {
        try {
            String userId = (String) httpRequest.getAttribute("userId");
            
            if (userId == null) {
                return ResponseEntity.status(401)
                    .body(Map.of("error", "Unauthorized"));
            }
            
            String currentPassword = request.get("current_password");
            String newPassword = request.get("new_password");
            
            if (currentPassword == null || newPassword == null) {
                return ResponseEntity.status(400)
                    .body(Map.of("error", "Current password and new password are required"));
            }
            
            authService.changePassword(userId, currentPassword, newPassword);
            return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(400)
                .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Failed to change password"));
        }
    }
} 