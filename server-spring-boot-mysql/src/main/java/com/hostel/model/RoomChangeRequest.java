package com.hostel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "room_change_requests")
public class RoomChangeRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonProperty("student_id")
    @Column(name = "student_id")
    private Long studentId;
    
    @JsonProperty("current_room_id")
    @Column(name = "current_room_id")
    private Long currentRoomId;
    
    @JsonProperty("requested_room_id")
    @Column(name = "requested_room_id")
    private Long requestedRoomId;
    
    @JsonProperty("requested_bed_number")
    @Column(name = "requested_bed_number")
    private Integer requestedBedNumber;
    
    @Column(columnDefinition = "TEXT")
    private String reason;
    
    private String status; // "pending", "approved", "rejected"
    
    @CreationTimestamp
    @JsonProperty("requested_at")
    @Column(name = "requested_at", updatable = false)
    private LocalDateTime requestedAt;
    
    @JsonProperty("processed_at")
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @JsonProperty("processed_by")
    @Column(name = "processed_by")
    private String processedBy;
    
    // Constructors
    public RoomChangeRequest() {
        this.status = "pending";
    }
    
    public RoomChangeRequest(Long studentId, Long currentRoomId, Long requestedRoomId, Integer requestedBedNumber, String reason) {
        this();
        this.studentId = studentId;
        this.currentRoomId = currentRoomId;
        this.requestedRoomId = requestedRoomId;
        this.requestedBedNumber = requestedBedNumber;
        this.reason = reason;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
    
    public Long getCurrentRoomId() {
        return currentRoomId;
    }
    
    public void setCurrentRoomId(Long currentRoomId) {
        this.currentRoomId = currentRoomId;
    }
    
    public Long getRequestedRoomId() {
        return requestedRoomId;
    }
    
    public void setRequestedRoomId(Long requestedRoomId) {
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