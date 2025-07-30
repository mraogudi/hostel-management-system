package com.hostel.controller;

import com.hostel.model.FoodMenu;
import com.hostel.service.FoodMenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class FoodMenuController {

    private final FoodMenuService foodMenuService;

    public FoodMenuController(FoodMenuService foodMenuService) {
        this.foodMenuService = foodMenuService;
    }

    @GetMapping("/food-menu")
    public ResponseEntity<?> getFoodMenu() {
        try {
            List<FoodMenu> sortedMenu = foodMenuService.getFoodMenu();
            return ResponseEntity.ok(sortedMenu);
            
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(Map.of("error", "Database error"));
        }
    }
} 