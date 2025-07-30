package com.hostel.dto;

import jakarta.validation.constraints.NotBlank;

public class RoomChangeRequestDto {
    
    @NotBlank(message = "Requested room ID is required")
    private String requestedRoomId;
    
    @NotBlank(message = "Reason is required")
    private String reason;
    
    // Constructors
    public RoomChangeRequestDto() {}
    
    public RoomChangeRequestDto(String requestedRoomId, String reason) {
        this.requestedRoomId = requestedRoomId;
        this.reason = reason;
    }
    
    // Getters and Setters
    public String getRequestedRoomId() {
        return requestedRoomId;
    }
    
    public void setRequestedRoomId(String requestedRoomId) {
        this.requestedRoomId = requestedRoomId;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
} 