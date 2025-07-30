package com.hostel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Document(collection = "rooms")
public class Room {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String roomNumber;
    
    private Integer floor;
    
    private Integer capacity;
    
    private Integer occupiedBeds;
    
    private String roomType;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    // Constructors
    public Room() {}
    
    public Room(String roomNumber, Integer floor, Integer capacity, String roomType) {
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.capacity = capacity;
        this.occupiedBeds = 0;
        this.roomType = roomType;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getRoomNumber() {
        return roomNumber;
    }
    
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }
    
    public Integer getFloor() {
        return floor;
    }
    
    public void setFloor(Integer floor) {
        this.floor = floor;
    }
    
    public Integer getCapacity() {
        return capacity;
    }
    
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    
    public Integer getOccupiedBeds() {
        return occupiedBeds;
    }
    
    public void setOccupiedBeds(Integer occupiedBeds) {
        this.occupiedBeds = occupiedBeds;
    }
    
    public String getRoomType() {
        return roomType;
    }
    
    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 