package com.hostel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "room_change_requests")
public class RoomChangeRequest {
    
    @Id
    private String id;
    
    @JsonProperty("student_id")
    private String studentId;
    
    @JsonProperty("current_room_id")
    private String currentRoomId;
    
    @JsonProperty("requested_room_id")
    private String requestedRoomId;
    
    @JsonProperty("requested_bed_number")
    private Integer requestedBedNumber;
    
    private String reason;
    
    private String status; // "pending", "approved", "rejected"
    
    @CreatedDate
    @JsonProperty("requested_at")
    private LocalDateTime requestedAt;
    
    @JsonProperty("processed_at")
    private LocalDateTime processedAt;
    
    @JsonProperty("processed_by")
    private String processedBy;
    
    // Constructors
    public RoomChangeRequest() {
        this.status = "pending";
        this.requestedAt = LocalDateTime.now();
    }
    
    public RoomChangeRequest(String studentId, String currentRoomId, String requestedRoomId, Integer requestedBedNumber, String reason) {
        this();
        this.studentId = studentId;
        this.currentRoomId = currentRoomId;
        this.requestedRoomId = requestedRoomId;
        this.requestedBedNumber = requestedBedNumber;
        this.reason = reason;
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