package com.hostel.repository;

import com.hostel.model.FoodMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodMenuRepository extends JpaRepository<FoodMenu, Long> {
    
    List<FoodMenu> findByDayOfWeek(String dayOfWeek);
    
    List<FoodMenu> findByMealType(String mealType);
    
    List<FoodMenu> findByDayOfWeekAndMealType(String dayOfWeek, String mealType);
} 