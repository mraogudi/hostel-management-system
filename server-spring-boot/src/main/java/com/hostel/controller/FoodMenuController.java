package com.hostel.controller;

import com.hostel.model.FoodMenu;
import com.hostel.service.FoodMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Food Menu", description = "Endpoints for hostel food menu information")
public class FoodMenuController {

    private final FoodMenuService foodMenuService;

    public FoodMenuController(FoodMenuService foodMenuService) {
        this.foodMenuService = foodMenuService;
    }

    @GetMapping("/food-menu")
    @Operation(
        summary = "Get Food Menu", 
        description = "Retrieve the weekly food menu with meals organized by day and meal type"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Food menu retrieved successfully",
            content = @Content(schema = @Schema(implementation = FoodMenu.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
            content = @Content(schema = @Schema(example = "{\"error\": \"Database error\"}")))
    })
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