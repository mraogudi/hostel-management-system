package com.hostel.service;

import com.hostel.model.*;
import com.hostel.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DataInitializationService {
    
    private final UserRepository userRepository;
    
    private final RoomRepository roomRepository;
    
    private final BedRepository bedRepository;
    
    private final FoodMenuRepository foodMenuRepository;
    
    private final PasswordEncoder passwordEncoder;

    public DataInitializationService(UserRepository userRepository, RoomRepository roomRepository, BedRepository bedRepository, FoodMenuRepository foodMenuRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.foodMenuRepository = foodMenuRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void initializeDatabase() {
        // Check if data already exists
        if (userRepository.count() > 0) {
            System.out.println("Database already initialized, skipping data initialization");
            return;
        }
        
        System.out.println("Initializing database with default data...");
        
        // Create default warden account
        createDefaultWarden();
        
        // Create rooms and beds
        createRoomsAndBeds();
        
        // Create sample food menu
        createFoodMenu();
        
        System.out.println("Database initialization completed!");
    }
    
    private void createDefaultWarden() {
        String hashedPassword = passwordEncoder.encode("warden123");
        
        User warden = new User(
            "warden",
            hashedPassword,
            "warden",
            "Hostel Warden",
            "warden@hostel.edu",
            null
        );
        
        userRepository.save(warden);
        System.out.println("Default warden account created");
    }
    
    private void createRoomsAndBeds() {
        for (int i = 1; i <= 10; i++) {
            String roomNumber = String.format("R%03d", i);
            int floor = (int) Math.ceil(i / 4.0);
            
            Room room = new Room(roomNumber, floor, 3, "standard");
            Room savedRoom = roomRepository.save(room);
            
            // Create 3 beds for each room
            for (int bedNum = 1; bedNum <= 3; bedNum++) {
                Bed bed = new Bed(savedRoom.getId(), bedNum);
                bedRepository.save(bed);
            }
        }
        System.out.println("Rooms and beds created");
    }
    
    private void createFoodMenu() {
        List<FoodMenu> menuItems = Arrays.asList(
            new FoodMenu("breakfast", "Monday", "Bread, Butter, Jam, Tea/Coffee, Boiled Eggs"),
            new FoodMenu("lunch", "Monday", "Rice, Dal, Vegetable Curry, Chapati, Pickle"),
            new FoodMenu("dinner", "Monday", "Rice, Sambar, Dry Vegetable, Chapati, Curd"),
            
            new FoodMenu("breakfast", "Tuesday", "Poha, Tea/Coffee, Banana"),
            new FoodMenu("lunch", "Tuesday", "Rice, Rasam, Vegetable Curry, Chapati, Papad"),
            new FoodMenu("dinner", "Tuesday", "Rice, Dal, Mixed Vegetable, Chapati, Pickle"),
            
            new FoodMenu("breakfast", "Wednesday", "Idli, Sambar, Chutney, Tea/Coffee"),
            new FoodMenu("lunch", "Wednesday", "Rice, Curd, Vegetable, Chapati, Pickle"),
            new FoodMenu("dinner", "Wednesday", "Rice, Dal, Fry, Chapati, Salad"),
            
            new FoodMenu("breakfast", "Thursday", "Paratha, Curd, Pickle, Tea/Coffee"),
            new FoodMenu("lunch", "Thursday", "Rice, Dal, Vegetable Curry, Chapati, Papad"),
            new FoodMenu("dinner", "Thursday", "Rice, Sambar, Vegetable, Chapati, Curd"),
            
            new FoodMenu("breakfast", "Friday", "Upma, Chutney, Tea/Coffee"),
            new FoodMenu("lunch", "Friday", "Rice, Rasam, Vegetable, Chapati, Pickle"),
            new FoodMenu("dinner", "Friday", "Rice, Dal, Dry Vegetable, Chapati, Salad"),
            
            new FoodMenu("breakfast", "Saturday", "Dosa, Sambar, Chutney, Tea/Coffee"),
            new FoodMenu("lunch", "Saturday", "Rice, Dal, Mixed Vegetable, Chapati, Papad"),
            new FoodMenu("dinner", "Saturday", "Rice, Curd, Vegetable, Chapati, Pickle"),
            
            new FoodMenu("breakfast", "Sunday", "Puri, Aloo Sabzi, Tea/Coffee"),
            new FoodMenu("lunch", "Sunday", "Rice, Dal, Special Curry, Chapati, Sweet"),
            new FoodMenu("dinner", "Sunday", "Rice, Sambar, Vegetable, Chapati, Curd")
        );
        
        foodMenuRepository.saveAll(menuItems);
        System.out.println("Food menu created");
    }
} 