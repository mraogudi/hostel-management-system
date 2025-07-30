package com.hostel.service;

import com.hostel.model.FoodMenu;
import com.hostel.repository.FoodMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodMenuService {
    
    @Autowired
    private FoodMenuRepository foodMenuRepository;
    
    private static final List<String> DAY_ORDER = Arrays.asList(
        "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
    );
    
    private static final List<String> MEAL_ORDER = Arrays.asList(
        "breakfast", "lunch", "dinner"
    );
    
    public List<FoodMenu> getFoodMenu() {
        List<FoodMenu> menuItems = foodMenuRepository.findAll();
        
        // Sort by day of week and meal type
        return menuItems.stream()
            .sorted(Comparator
                .comparing((FoodMenu item) -> DAY_ORDER.indexOf(item.getDayOfWeek()))
                .thenComparing(item -> MEAL_ORDER.indexOf(item.getMealType())))
            .collect(Collectors.toList());
    }
    
    public FoodMenu createFoodMenuItem(String mealType, String dayOfWeek, String items) {
        FoodMenu foodMenu = new FoodMenu(mealType, dayOfWeek, items);
        return foodMenuRepository.save(foodMenu);
    }
    
    public FoodMenu updateFoodMenuItem(String id, String mealType, String dayOfWeek, String items) {
        FoodMenu foodMenu = foodMenuRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Food menu item not found"));
        
        foodMenu.setMealType(mealType);
        foodMenu.setDayOfWeek(dayOfWeek);
        foodMenu.setItems(items);
        
        return foodMenuRepository.save(foodMenu);
    }
    
    public void deleteFoodMenuItem(String id) {
        if (!foodMenuRepository.existsById(id)) {
            throw new RuntimeException("Food menu item not found");
        }
        foodMenuRepository.deleteById(id);
    }
    
    public List<FoodMenu> getFoodMenuByDay(String dayOfWeek) {
        List<FoodMenu> menuItems = foodMenuRepository.findByDayOfWeek(dayOfWeek);
        
        return menuItems.stream()
            .sorted(Comparator.comparing(item -> MEAL_ORDER.indexOf(item.getMealType())))
            .collect(Collectors.toList());
    }
    
    public List<FoodMenu> getFoodMenuByMealType(String mealType) {
        List<FoodMenu> menuItems = foodMenuRepository.findByMealType(mealType);
        
        return menuItems.stream()
            .sorted(Comparator.comparing(item -> DAY_ORDER.indexOf(item.getDayOfWeek())))
            .collect(Collectors.toList());
    }
} 