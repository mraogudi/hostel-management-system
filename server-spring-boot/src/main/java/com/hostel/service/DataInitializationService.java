package com.hostel.service;

import com.hostel.model.*;
import com.hostel.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
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

    @PostConstruct
    public void initializeData() {
        // Create default warden if not exists
        if (userRepository.findByUsername("warden").isEmpty()) {
            User warden = new User("warden", passwordEncoder.encode("warden123"), "warden", "System Warden", "warden@hostel.com", "9999999999");
            userRepository.save(warden);
            System.out.println("Default warden created: username=warden, password=warden123");
        }

        // Create sample rooms if none exist
        if (roomRepository.count() == 0) {
            createSampleRoomsWithBeds();
        }

        // Create sample food menu if none exists
        if (foodMenuRepository.count() == 0) {
            createFoodMenu();
        }
    }
    
    // Public method to force data reinitialization (for debugging)
    public void forceReinitializeData() {
        System.out.println("=== FORCE REINITIALIZING DATA ===");
        
        // Clear existing data
        bedRepository.deleteAll();
        roomRepository.deleteAll();
        
        // Recreate rooms and beds
        createSampleRoomsWithBeds();
        
        System.out.println("=== FORCE REINITIALIZATION COMPLETE ===");
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
    
    private void createSampleRoomsWithBeds() {
        System.out.println("Creating sample rooms with beds...");
        
        // Create rooms - specifically ensuring R007 exists with 4 beds
        Room room1 = new Room("R001", 1, 4, "Standard");
        Room room2 = new Room("R002", 1, 4, "Standard");
        Room room3 = new Room("R003", 1, 2, "Premium");
        Room room4 = new Room("R004", 2, 4, "Standard");
        Room room5 = new Room("R005", 2, 4, "Standard");
        Room room6 = new Room("R006", 2, 2, "Premium");
        Room room7 = new Room("R007", 2, 4, "Standard"); // This is the room from the screenshot
        Room room8 = new Room("R008", 3, 4, "Standard");
        
        // Save rooms first
        List<Room> rooms = Arrays.asList(room1, room2, room3, room4, room5, room6, room7, room8);
        List<Room> savedRooms = roomRepository.saveAll(rooms);
        
        // Create beds for each room with proper status
        for (Room room : savedRooms) {
            System.out.println("Creating beds for room " + room.getRoomNumber() + " with capacity " + room.getCapacity());
            for (int bedNumber = 1; bedNumber <= room.getCapacity(); bedNumber++) {
                Bed bed = new Bed(room.getId(), bedNumber);
                // Ensure bed status is explicitly set to "available"
                bed.setStatus("available");
                Bed savedBed = bedRepository.save(bed);
                System.out.println("Created bed " + bedNumber + " for room " + room.getRoomNumber() + " with status: " + savedBed.getStatus());
            }
        }
        
        System.out.println("Sample rooms and beds created successfully!");
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