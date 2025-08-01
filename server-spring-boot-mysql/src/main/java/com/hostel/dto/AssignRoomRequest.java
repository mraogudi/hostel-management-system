package com.hostel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AssignRoomRequest {
    
    @NotBlank(message = "Student roll number is required")
    private String studentId; // This will contain the roll number
    
    @NotNull(message = "Room ID is required")
    private Long roomId;
    
    @NotNull(message = "Bed number is required")
    private Integer bedNumber;
    
    // Constructors
    public AssignRoomRequest() {}
    
    public AssignRoomRequest(String studentId, Long roomId, Integer bedNumber) {
        this.studentId = studentId;
        this.roomId = roomId;
        this.bedNumber = bedNumber;
    }
    
    // Getters and Setters
    public String getStudentId() {
        return studentId; // Returns roll number
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId; // Sets roll number
    }
    
    public Long getRoomId() {
        return roomId;
    }
    
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }
    
    public Integer getBedNumber() {
        return bedNumber;
    }
    
    public void setBedNumber(Integer bedNumber) {
        this.bedNumber = bedNumber;
    }
} 