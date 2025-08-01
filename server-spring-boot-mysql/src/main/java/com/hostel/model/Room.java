package com.hostel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "room_number", unique = true, nullable = false)
    @JsonProperty("room_number")
    private String roomNumber;
    
    private Integer floor;
    
    private Integer capacity;
    
    @JsonProperty("occupied_beds")
    @Column(name = "occupied_beds")
    private Integer occupiedBeds;
    
    @JsonProperty("room_type")
    @Column(name = "room_type")
    private String roomType;
    
    @CreationTimestamp
    @JsonProperty("created_at")
    @Column(name = "created_at", updatable = false)
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
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
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