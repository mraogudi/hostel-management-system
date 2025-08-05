package com.hostel.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hostel.model.*;
import com.hostel.repository.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class JsonDataLoaderService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final FoodMenuRepository foodMenuRepository;
    private final RoomChangeRequestRepository roomChangeRequestRepository;
    private final PersonalDetailsUpdateRequestRepository personalDetailsUpdateRequestRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public JsonDataLoaderService(
            UserRepository userRepository,
            RoomRepository roomRepository,
            BedRepository bedRepository,
            FoodMenuRepository foodMenuRepository,
            RoomChangeRequestRepository roomChangeRequestRepository,
            PersonalDetailsUpdateRequestRepository personalDetailsUpdateRequestRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.bedRepository = bedRepository;
        this.foodMenuRepository = foodMenuRepository;
        this.roomChangeRequestRepository = roomChangeRequestRepository;
        this.personalDetailsUpdateRequestRepository = personalDetailsUpdateRequestRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public void loadDataFromJson() {
        try {
            ClassPathResource resource = new ClassPathResource("database.json");
            if (!resource.exists()) {
                System.out.println("database.json not found in resources. Skipping JSON data loading.");
                return;
            }

            System.out.println("📊 Loading database from JSON file...");
            
            JsonNode rootNode = objectMapper.readTree(resource.getInputStream());
            
            // Load data in order: users, rooms, beds, food_menu, requests
            loadUsers(rootNode.get("users"));
            loadRooms(rootNode.get("rooms"));
            loadBeds(rootNode.get("beds"));
            loadFoodMenu(rootNode.get("food_menu"));
            loadRoomChangeRequests(rootNode.get("room_change_requests"));
            loadPersonalDetailsUpdateRequests(rootNode.get("personal_details_update_requests"));
            
            displayStatistics();
            
        } catch (IOException e) {
            System.err.println("❌ Error loading data from JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadUsers(JsonNode usersNode) {
        if (usersNode == null || !usersNode.isArray()) return;
        
        int loaded = 0;
        for (JsonNode userNode : usersNode) {
            try {
                User user = new User();
                user.setId(userNode.get("id").asText());
                user.setUsername(userNode.get("username").asText());
                
                // Check if user already exists
                if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                    continue;
                }
                
                user.setPassword(userNode.get("password").asText());
                user.setRole(userNode.get("role").asText());
                user.setFullName(userNode.get("full_name").asText());
                user.setEmail(userNode.get("email").asText());
                user.setPhone(userNode.get("phone").asText());
                
                if (userNode.has("date_of_birth") && !userNode.get("date_of_birth").isNull()) {
                    user.setDateOfBirth(LocalDate.parse(userNode.get("date_of_birth").asText()));
                }
                if (userNode.has("gender")) user.setGender(userNode.get("gender").asText());
                if (userNode.has("aadhaar_id")) user.setAadhaarId(userNode.get("aadhaar_id").asText());
                if (userNode.has("roll_no")) user.setRollNo(userNode.get("roll_no").asText());
                if (userNode.has("stream")) user.setStream(userNode.get("stream").asText());
                if (userNode.has("branch")) user.setBranch(userNode.get("branch").asText());
                if (userNode.has("address_line1")) user.setAddressLine1(userNode.get("address_line1").asText());
                if (userNode.has("address_line2")) user.setAddressLine2(userNode.get("address_line2").asText());
                if (userNode.has("city")) user.setCity(userNode.get("city").asText());
                if (userNode.has("state")) user.setState(userNode.get("state").asText());
                if (userNode.has("postal_code")) user.setPostalCode(userNode.get("postal_code").asText());
                if (userNode.has("guardian_name")) user.setGuardianName(userNode.get("guardian_name").asText());
                if (userNode.has("guardian_phone")) user.setGuardianPhone(userNode.get("guardian_phone").asText());
                if (userNode.has("guardian_address")) user.setGuardianAddress(userNode.get("guardian_address").asText());
                if (userNode.has("first_login")) user.setFirstLogin(userNode.get("first_login").asBoolean());
                if (userNode.has("created_at")) {
                    user.setCreatedAt(LocalDateTime.parse(userNode.get("created_at").asText()));
                }
                
                userRepository.save(user);
                loaded++;
                
            } catch (Exception e) {
                System.err.println("Error loading user: " + e.getMessage());
            }
        }
        System.out.println("   - Users: " + loaded + " loaded");
    }

    private void loadRooms(JsonNode roomsNode) {
        if (roomsNode == null || !roomsNode.isArray()) return;
        
        int loaded = 0;
        for (JsonNode roomNode : roomsNode) {
            try {
                Room room = new Room();
                room.setId(roomNode.get("id").asText());
                room.setRoomNumber(roomNode.get("room_number").asText());
                
                // Check if room already exists
                if (roomRepository.findByRoomNumber(room.getRoomNumber()).isPresent()) {
                    continue;
                }
                
                room.setFloor(roomNode.get("floor").asInt());
                room.setCapacity(roomNode.get("capacity").asInt());
                room.setOccupiedBeds(roomNode.get("occupied_beds").asInt());
                room.setRoomType(roomNode.get("room_type").asText());
                if (roomNode.has("created_at")) {
                    room.setCreatedAt(LocalDateTime.parse(roomNode.get("created_at").asText()));
                }
                
                roomRepository.save(room);
                loaded++;
                
            } catch (Exception e) {
                System.err.println("Error loading room: " + e.getMessage());
            }
        }
        System.out.println("   - Rooms: " + loaded + " loaded");
    }

    private void loadBeds(JsonNode bedsNode) {
        if (bedsNode == null || !bedsNode.isArray()) return;
        
        int loaded = 0;
        for (JsonNode bedNode : bedsNode) {
            try {
                Bed bed = new Bed();
                bed.setId(bedNode.get("id").asText());
                bed.setRoomId(bedNode.get("room_id").asText());
                bed.setBedNumber(bedNode.get("bed_number").asInt());
                
                if (bedNode.has("student_id") && !bedNode.get("student_id").isNull()) {
                    bed.setStudentId(bedNode.get("student_id").asText());
                }
                bed.setStatus(bedNode.get("status").asText());
                
                bedRepository.save(bed);
                loaded++;
                
            } catch (Exception e) {
                System.err.println("Error loading bed: " + e.getMessage());
            }
        }
        System.out.println("   - Beds: " + loaded + " loaded");
    }

    private void loadFoodMenu(JsonNode foodMenuNode) {
        if (foodMenuNode == null || !foodMenuNode.isArray()) return;
        
        int loaded = 0;
        for (JsonNode menuNode : foodMenuNode) {
            try {
                FoodMenu foodMenu = new FoodMenu();
                foodMenu.setId(menuNode.get("id").asText());
                foodMenu.setMealType(menuNode.get("meal_type").asText());
                foodMenu.setDayOfWeek(menuNode.get("day_of_week").asText());
                foodMenu.setItems(menuNode.get("items").asText());
                if (menuNode.has("created_at")) {
                    foodMenu.setCreatedAt(LocalDateTime.parse(menuNode.get("created_at").asText()));
                }
                
                foodMenuRepository.save(foodMenu);
                loaded++;
                
            } catch (Exception e) {
                System.err.println("Error loading food menu: " + e.getMessage());
            }
        }
        System.out.println("   - Food Menu Items: " + loaded + " loaded");
    }

    private void loadRoomChangeRequests(JsonNode requestsNode) {
        if (requestsNode == null || !requestsNode.isArray()) return;
        
        int loaded = 0;
        for (JsonNode requestNode : requestsNode) {
            try {
                RoomChangeRequest request = new RoomChangeRequest();
                request.setId(requestNode.get("id").asText());
                request.setStudentId(requestNode.get("student_id").asText());
                request.setCurrentRoomId(requestNode.get("current_room_id").asText());
                request.setRequestedRoomId(requestNode.get("requested_room_id").asText());
                request.setRequestedBedNumber(requestNode.get("requested_bed_number").asInt());
                request.setReason(requestNode.get("reason").asText());
                request.setStatus(requestNode.get("status").asText());
                
                if (requestNode.has("requested_at")) {
                    request.setRequestedAt(LocalDateTime.parse(requestNode.get("requested_at").asText()));
                }
                if (requestNode.has("processed_at") && !requestNode.get("processed_at").isNull()) {
                    request.setProcessedAt(LocalDateTime.parse(requestNode.get("processed_at").asText()));
                }
                if (requestNode.has("processed_by") && !requestNode.get("processed_by").isNull()) {
                    request.setProcessedBy(requestNode.get("processed_by").asText());
                }
                
                roomChangeRequestRepository.save(request);
                loaded++;
                
            } catch (Exception e) {
                System.err.println("Error loading room change request: " + e.getMessage());
            }
        }
        System.out.println("   - Room Change Requests: " + loaded + " loaded");
    }

    private void loadPersonalDetailsUpdateRequests(JsonNode requestsNode) {
        if (requestsNode == null || !requestsNode.isArray()) return;
        
        int loaded = 0;
        for (JsonNode requestNode : requestsNode) {
            try {
                PersonalDetailsUpdateRequest request = new PersonalDetailsUpdateRequest();
                request.setId(requestNode.get("id").asText());
                request.setStudentId(requestNode.get("student_id").asText());
                request.setStudentName(requestNode.get("student_name").asText());
                request.setStudentRollNo(requestNode.get("student_roll_no").asText());
                
                if (requestNode.has("phone")) request.setPhone(requestNode.get("phone").asText());
                if (requestNode.has("address_line1")) request.setAddressLine1(requestNode.get("address_line1").asText());
                if (requestNode.has("address_line2")) request.setAddressLine2(requestNode.get("address_line2").asText());
                if (requestNode.has("city")) request.setCity(requestNode.get("city").asText());
                if (requestNode.has("state")) request.setState(requestNode.get("state").asText());
                if (requestNode.has("postal_code")) request.setPostalCode(requestNode.get("postal_code").asText());
                if (requestNode.has("guardian_name")) request.setGuardianName(requestNode.get("guardian_name").asText());
                if (requestNode.has("guardian_phone")) request.setGuardianPhone(requestNode.get("guardian_phone").asText());
                if (requestNode.has("guardian_address")) request.setGuardianAddress(requestNode.get("guardian_address").asText());
                
                request.setStatus(requestNode.get("status").asText());
                
                if (requestNode.has("warden_comments") && !requestNode.get("warden_comments").isNull()) {
                    request.setWardenComments(requestNode.get("warden_comments").asText());
                }
                if (requestNode.has("processed_at") && !requestNode.get("processed_at").isNull()) {
                    request.setProcessedAt(LocalDateTime.parse(requestNode.get("processed_at").asText()));
                }
                if (requestNode.has("processed_by") && !requestNode.get("processed_by").isNull()) {
                    request.setProcessedBy(requestNode.get("processed_by").asText());
                }
                if (requestNode.has("created_at")) {
                    request.setCreatedAt(LocalDateTime.parse(requestNode.get("created_at").asText()));
                }
                if (requestNode.has("updated_at") && !requestNode.get("updated_at").isNull()) {
                    request.setUpdatedAt(LocalDateTime.parse(requestNode.get("updated_at").asText()));
                }
                
                personalDetailsUpdateRequestRepository.save(request);
                loaded++;
                
            } catch (Exception e) {
                System.err.println("Error loading personal details request: " + e.getMessage());
            }
        }
        System.out.println("   - Personal Details Requests: " + loaded + " loaded");
    }

    private void displayStatistics() {
        long userCount = userRepository.count();
        long wardenCount = userRepository.countByRole("warden");
        long studentCount = userRepository.countByRole("student");
        long roomCount = roomRepository.count();
        long bedCount = bedRepository.count();
        long occupiedBeds = bedRepository.countByStatus("occupied");
        long availableBeds = bedRepository.countByStatus("available");
        long foodMenuCount = foodMenuRepository.count();
        long roomChangeRequestCount = roomChangeRequestRepository.count();
        long personalDetailsRequestCount = personalDetailsUpdateRequestRepository.count();

        System.out.println("✅ Database loaded successfully from JSON!");
        System.out.println("📊 Database statistics:");
        System.out.println("   - Users: " + userCount + " (" + wardenCount + " wardens, " + studentCount + " students)");
        System.out.println("   - Rooms: " + roomCount);
        System.out.println("   - Beds: " + bedCount + " (" + occupiedBeds + " occupied, " + availableBeds + " available)");
        System.out.println("   - Food Menu Items: " + foodMenuCount);
        System.out.println("   - Room Change Requests: " + roomChangeRequestCount);
        System.out.println("   - Personal Details Requests: " + personalDetailsRequestCount);

        // Find and display default login credentials
        userRepository.findByRole("warden").stream().findFirst().ifPresent(warden -> {
            System.out.println("\n🔐 Default Login Credentials:");
            System.out.println("   Warden - Username: " + warden.getUsername() + ", Password: warden123");
            
            List<User> students = userRepository.findByRole("student");
            students.stream().limit(3).forEach(student -> {
                System.out.println("   Student - Username: " + student.getUsername() + ", Password: password123");
            });
            if (students.size() > 3) {
                System.out.println("   ... and " + (students.size() - 3) + " more students");
            }
        });
    }
} 