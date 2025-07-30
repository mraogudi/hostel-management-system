package com.hostel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RoomChangeRequestDto {
    
    @NotBlank(message = "Requested room ID is required")
    private String requestedRoomId;
    
    @NotNull(message = "Requested bed number is required")
    private Integer requestedBedNumber;
    
    @NotBlank(message = "Reason is required")
    private String reason;
    
    // Constructors
    public RoomChangeRequestDto() {}
    
    public RoomChangeRequestDto(String requestedRoomId, Integer requestedBedNumber, String reason) {
        this.requestedRoomId = requestedRoomId;
        this.requestedBedNumber = requestedBedNumber;
        this.reason = reason;
    }
    
    // Getters and Setters
    public String getRequestedRoomId() {
        return requestedRoomId;
    }
    
    public void setRequestedRoomId(String requestedRoomId) {
        this.requestedRoomId = requestedRoomId;
    }
    
    public Integer getRequestedBedNumber() {
        return requestedBedNumber;
    }
    
    public void setRequestedBedNumber(Integer requestedBedNumber) {
        this.requestedBedNumber = requestedBedNumber;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
} 