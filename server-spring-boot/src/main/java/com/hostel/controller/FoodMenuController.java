package com.hostel.controller;

import com.hostel.model.FoodMenu;
import com.hostel.repository.FoodMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FoodMenuController {
    
    @Autowired
    private FoodMenuRepository foodMenuRepository;
    
    private static final List<String> DAY_ORDER = Arrays.asList(
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    );
    
    private static final List<String> MEAL_ORDER = Arrays.asList(
        "breakfast", "lunch", "dinner"
    );
    
    @GetMapping("/food-menu")
    public ResponseEntity<?> getFoodMenu() {
        try {
            List<FoodMenu> menuItems = foodMenuRepository.findAll();
            
            // Sort by day of week and meal type
            List<FoodMenu> sortedMenu = menuItems.stream()
                .sorted(Comparator
                    .comparing((FoodMenu item) -> DAY_ORDER.indexOf(item.getDayOfWeek()))
                    .thenComparing(item -> MEAL_ORDER.indexOf(item.getMealType())))
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(sortedMenu);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Database error"));
        }
    }
} 