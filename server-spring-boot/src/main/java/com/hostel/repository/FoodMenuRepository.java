package com.hostel.repository;

import com.hostel.model.FoodMenu;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FoodMenuRepository extends MongoRepository<FoodMenu, String> {
    
    List<FoodMenu> findByDayOfWeek(String dayOfWeek);
    
    List<FoodMenu> findByMealType(String mealType);
    
    List<FoodMenu> findByDayOfWeekAndMealType(String dayOfWeek, String mealType);
} 