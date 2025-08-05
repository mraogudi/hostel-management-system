package com.hostel.service;

import com.hostel.model.*;
import com.hostel.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DataInitializationService {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializationService.class);
    
    private final UserRepository userRepository;
    
    private final RoomRepository roomRepository;
    
    private final BedRepository bedRepository;
    
    private final FoodMenuRepository foodMenuRepository;
    
    private final JsonDataLoaderService jsonDataLoaderService;
    
    private final PasswordEncoder passwordEncoder;

    public DataInitializationService(UserRepository userRepository, RoomRepository roomRepository, BedRepository bedRepository, FoodMenuRepository foodMenuRepository, JsonDataLoaderService jsonDataLoaderService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.foodMenuRepository = foodMenuRepository;
        this.jsonDataLoaderService = jsonDataLoaderService;
        this.passwordEncoder = passwordEncoder;
    }

    public void initializeData() {
        // Try to load from JSON first
        if (userRepository.count() == 0 && roomRepository.count() == 0) {
            logger.info("🔄 No existing data found. Attempting to load from database.json...");
            try {
                jsonDataLoaderService.loadDataFromJson();
                return; // If JSON loading succeeds, we're done
            } catch (Exception e) {
                logger.warn("⚠️ JSON loading failed, falling back to default data initialization: {}", e.getMessage());
            }
        }
        
        // Fallback to original initialization logic
        // Create default warden if not exists
        if (userRepository.findByUsername("warden").isEmpty()) {
            createDefaultWarden();
        } else {
            // Update existing warden phone if null
            updateWardenPhone();
        }

        // Create sample rooms if none exist
        if (roomRepository.count() == 0) {
            createSampleRoomsWithBeds();
        }

        // Create sample food menu if none exists
        if (foodMenuRepository.count() == 0) {
            createFoodMenu();
        }
        
        // Display statistics for fallback data
        if (userRepository.count() > 0) {
            displayFallbackStatistics();
        }
    }

    private void createDefaultWarden() {
        String hashedPassword = passwordEncoder.encode("warden123");
        
        User warden = new User(
            "warden",
            hashedPassword,
            "warden",
            "Hostel Warden",
            "warden@hostel.edu",
            "9876543210"
        );
        
        userRepository.save(warden);
        logger.info("Default warden account created");
    }
    
    private void updateWardenPhone() {
        userRepository.findByUsername("warden").ifPresent(warden -> {
            if (warden.getPhone() == null || warden.getPhone().trim().isEmpty()) {
                warden.setPhone("9876543210");
                userRepository.save(warden);
                logger.info("Updated warden phone number");
            }
        });
    }
    
    private void createSampleRoomsWithBeds() {
        logger.info("Creating sample rooms with beds...");
        
        // Create rooms - specifically ensuring R007 exists with 4 beds
        List<Room> rooms = getRooms();
        List<Room> savedRooms = roomRepository.saveAll(rooms);
        
        // Create beds for each room with proper status
        for (Room room : savedRooms) {
            logger.debug("Creating beds for room {} with capacity {}", room.getRoomNumber(), room.getCapacity());
            for (int bedNumber = 1; bedNumber <= room.getCapacity(); bedNumber++) {
                Bed bed = new Bed(room.getId(), bedNumber);
                // Ensure bed status is explicitly set to "available"
                bed.setStatus("available");
                Bed savedBed = bedRepository.save(bed);
                logger.trace("Created bed {} for room {} with status: {}", bedNumber, room.getRoomNumber(), savedBed.getStatus());
            }
        }
        
        logger.info("Sample rooms and beds created successfully!");
    }

    private static List<Room> getRooms() {
        Room room1 = new Room("R001", 1, 4, "Standard");
        Room room2 = new Room("R002", 1, 4, "Standard");
        Room room3 = new Room("R003", 1, 2, "Premium");
        Room room4 = new Room("R004", 2, 4, "Standard");
        Room room5 = new Room("R005", 2, 4, "Standard");
        Room room6 = new Room("R006", 2, 2, "Premium");
        Room room7 = new Room("R007", 2, 4, "Standard"); // This is the room from the screenshot
        Room room8 = new Room("R008", 3, 4, "Standard");

        // Save rooms first
        return Arrays.asList(room1, room2, room3, room4, room5, room6, room7, room8);
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
        logger.info("Food menu created");
    }
    
    private void displayFallbackStatistics() {
        long userCount = userRepository.count();
        long roomCount = roomRepository.count();
        long bedCount = bedRepository.count();
        long foodMenuCount = foodMenuRepository.count();

        logger.info("✅ Fallback data initialization completed!");
        logger.info("📊 Database statistics:");
        logger.info("   - Users: {}", userCount);
        logger.info("   - Rooms: {}", roomCount);
        logger.info("   - Beds: {}", bedCount);
        logger.info("   - Food Menu Items: {}", foodMenuCount);
        logger.info("🔐 Default Login Credentials:");
        logger.info("   Warden - Username: warden, Password: warden123");
    }
} 