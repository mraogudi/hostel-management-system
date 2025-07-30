package com.hostel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "beds")
public class Bed {
    
    @Id
    private String id;
    
    @JsonProperty("room_id")
    private String roomId;
    
    @JsonProperty("bed_number")
    private Integer bedNumber;
    
    @JsonProperty("student_id")
    private String studentId;
    
    private String status; // "available" or "occupied"
    
    // Constructors
    public Bed() {}
    
    public Bed(String roomId, Integer bedNumber) {
        this.roomId = roomId;
        this.bedNumber = bedNumber;
        this.studentId = null;
        this.status = "available";
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getRoomId() {
        return roomId;
    }
    
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    
    public Integer getBedNumber() {
        return bedNumber;
    }
    
    public void setBedNumber(Integer bedNumber) {
        this.bedNumber = bedNumber;
    }
    
    public String getStudentId() {
        return studentId;
    }
    
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
} 