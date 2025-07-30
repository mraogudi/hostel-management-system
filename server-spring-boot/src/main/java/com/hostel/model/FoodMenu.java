package com.hostel.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "food_menu")
public class FoodMenu {
    
    @Id
    private String id;
    
    @JsonProperty("meal_type")
    private String mealType; // "breakfast", "lunch", "dinner"
    
    @JsonProperty("day_of_week")
    private String dayOfWeek; // "Monday", "Tuesday", etc.
    
    private String items;
    
    @CreatedDate
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public FoodMenu() {}
    
    public FoodMenu(String mealType, String dayOfWeek, String items) {
        this.mealType = mealType;
        this.dayOfWeek = dayOfWeek;
        this.items = items;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
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