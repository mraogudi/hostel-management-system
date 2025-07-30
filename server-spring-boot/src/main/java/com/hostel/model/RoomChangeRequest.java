package com.hostel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "room_change_requests")
public class RoomChangeRequest {
    
    @Id
    private String id;
    
    private String studentId;
    
    private String currentRoomId;
    
    private String requestedRoomId;
    
    private String reason;
    
    private String status; // "pending", "approved", "rejected"
    
    private LocalDateTime requestedAt;
    
    private LocalDateTime processedAt;
    
    private String processedBy;
    
    // Constructors
    public RoomChangeRequest() {}
    
    public RoomChangeRequest(String studentId, String currentRoomId, String requestedRoomId, String reason) {
        this.studentId = studentId;
        this.currentRoomId = currentRoomId;
        this.requestedRoomId = requestedRoomId;
        this.reason = reason;
        this.status = "pending";
        this.requestedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getCurrentRoomId() {
        return currentRoomId;
    }
    
    public void setCurrentRoomId(String currentRoomId) {
        this.currentRoomId = currentRoomId;
    }
    
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }
    
    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }
    
    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }
    
    public String getProcessedBy() {
        return processedBy;
    }
    
    public void setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
    }
} 