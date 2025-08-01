package com.hostel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "food_menu")
public class FoodMenu {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @JsonProperty("meal_type")
    @Column(name = "meal_type")
    private String mealType; // "breakfast", "lunch", "dinner"
    
    @JsonProperty("day_of_week")
    @Column(name = "day_of_week")
    private String dayOfWeek; // "Monday", "Tuesday", etc.
    
    @Column(columnDefinition = "TEXT")
    private String items;
    
    @CreationTimestamp
    @JsonProperty("created_at")
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public FoodMenu() {}
    
    public FoodMenu(String mealType, String dayOfWeek, String items) {
        this.mealType = mealType;
        this.dayOfWeek = dayOfWeek;
        this.items = items;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getMealType() {
        return mealType;
    }
    
    public void setMealType(String mealType) {
        this.mealType = mealType;
    }
    
    public String getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public String getItems() {
        return items;
    }
    
    public void setItems(String items) {
        this.items = items;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 