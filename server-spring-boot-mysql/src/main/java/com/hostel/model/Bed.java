package com.hostel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "beds")
public class Bed {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonProperty("room_id")
    @Column(name = "room_id")
    private Long roomId;
    
    @JsonProperty("bed_number")
    @Column(name = "bed_number")
    private Integer bedNumber;
    
    @JsonProperty("student_id")
    @Column(name = "student_id")
    private Long studentId;
    
    private String status; // "available" or "occupied"
    
    // Constructors
    public Bed() {}
    
    public Bed(Long roomId, Integer bedNumber) {
        this.roomId = roomId;
        this.bedNumber = bedNumber;
        this.studentId = null;
        this.status = "available";
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public Long getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
} 