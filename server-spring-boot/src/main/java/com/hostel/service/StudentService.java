package com.hostel.service;

import com.hostel.dto.CreateStudentRequest;
import com.hostel.dto.RoomChangeRequestDto;
import com.hostel.model.User;
import com.hostel.model.Bed;
import com.hostel.model.Room;
import com.hostel.model.RoomChangeRequest;
import com.hostel.repository.UserRepository;
import com.hostel.repository.BedRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.repository.RoomChangeRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BedRepository bedRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private RoomChangeRequestRepository roomChangeRequestRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();
    
    public Map<String, Object> createStudent(CreateStudentRequest request) {
        // Validation
        validateStudentRequest(request);
        
        // Generate password
        String password = generateRandomPassword(8);
        String hashedPassword = passwordEncoder.encode(password);
        
        // Create user with roll number as username
        User newStudent = new User(
            request.getRollNo(), // username = roll number
            hashedPassword,
            "student",
            request.getFullName(),
            request.getEmail(),
            request.getPhone(),
            request.getDateOfBirth(),
            request.getGender(),
            request.getAadhaarId(),
            request.getRollNo(),
            request.getStream(),
            request.getBranch()
        );
        
        User savedStudent = userRepository.save(newStudent);
        
        // Prepare response
        Map<String, Object> studentInfo = new HashMap<>();
        studentInfo.put("id", savedStudent.getId());
        studentInfo.put("username", savedStudent.getUsername());
        studentInfo.put("full_name", savedStudent.getFullName());
        studentInfo.put("email", savedStudent.getEmail());
        studentInfo.put("phone", savedStudent.getPhone());
        studentInfo.put("roll_no", savedStudent.getRollNo());
        studentInfo.put("stream", savedStudent.getStream());
        studentInfo.put("branch", savedStudent.getBranch());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Student created successfully");
        response.put("credentials", Map.of(
            "username", request.getRollNo(),
            "password", password
        ));
        response.put("student", studentInfo);
        
        return response;
    }
    
    public void assignRoom(String studentId, String roomId, Integer bedNumber) {
        Optional<Bed> bedOptional = bedRepository.findByRoomIdAndBedNumber(roomId, bedNumber);
        
        if (bedOptional.isEmpty() || !"available".equals(bedOptional.get().getStatus())) {
            throw new RuntimeException("Bed not available");
        }
        
        Bed bed = bedOptional.get();
        bed.setStudentId(studentId);
        bed.setStatus("occupied");
        bedRepository.save(bed);
        
        // Update room occupied beds count
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room != null) {
            long occupiedCount = bedRepository.countByRoomIdAndStatus(roomId, "occupied");
            room.setOccupiedBeds((int) occupiedCount);
            roomRepository.save(room);
        }
    }
    
    public void submitRoomChangeRequest(String userId, RoomChangeRequestDto requestDto) {
        // Get current room of student
        Optional<Bed> currentBedOptional = bedRepository.findByStudentId(userId);
        String currentRoomId = currentBedOptional.map(Bed::getRoomId).orElse(null);
        
        RoomChangeRequest roomChangeRequest = new RoomChangeRequest(
            userId,
            currentRoomId,
            requestDto.getRequestedRoomId(),
            requestDto.getReason()
        );
        
        roomChangeRequestRepository.save(roomChangeRequest);
    }
    
    public Map<String, Object> getStudentRoom(String userId) {
        Optional<Bed> myBedOptional = bedRepository.findByStudentId(userId);
        
        if (myBedOptional.isEmpty()) {
            throw new RuntimeException("No room assigned");
        }
        
        Bed myBed = myBedOptional.get();
        Optional<Room> roomOptional = roomRepository.findById(myBed.getRoomId());
        
        if (roomOptional.isEmpty()) {
            throw new RuntimeException("Room not found");
        }
        
        Room room = roomOptional.get();
        
        // Get roommates
        List<Bed> roomBeds = bedRepository.findByRoomId(myBed.getRoomId());
        List<Map<String, Object>> roommates = roomBeds.stream()
            .filter(bed -> bed.getStudentId() != null && !bed.getStudentId().equals(userId))
            .map(bed -> {
                User student = userRepository.findById(bed.getStudentId()).orElse(null);
                Map<String, Object> roommateMap = new HashMap<>();
                roommateMap.put("full_name", student != null ? student.getFullName() : "Unknown");
                return roommateMap;
            })
            .collect(Collectors.toList());
        
        Map<String, Object> myRoomDetails = new HashMap<>();
        myRoomDetails.put("id", room.getId());
        myRoomDetails.put("room_number", room.getRoomNumber());
        myRoomDetails.put("floor", room.getFloor());
        myRoomDetails.put("capacity", room.getCapacity());
        myRoomDetails.put("room_type", room.getRoomType());
        myRoomDetails.put("bed_number", myBed.getBedNumber());
        myRoomDetails.put("roommates", roommates);
        
        return myRoomDetails;
    }
    
    public List<RoomChangeRequest> getAllRoomChangeRequests() {
        return roomChangeRequestRepository.findAllByOrderByRequestedAtDesc();
    }
    
    private void validateStudentRequest(CreateStudentRequest request) {
        // Check if username (roll number) already exists
        if (userRepository.existsByUsername(request.getRollNo())) {
            throw new RuntimeException("Roll number already exists (roll number is used as username)");
        }
        
        // Check if Aadhaar ID already exists
        if (userRepository.findByAadhaarId(request.getAadhaarId()).isPresent()) {
            throw new RuntimeException("Aadhaar ID already exists");
        }
        
        // Check if Roll Number already exists (additional check)
        if (userRepository.findByRollNo(request.getRollNo()).isPresent()) {
            throw new RuntimeException("Roll number already exists");
        }
        
        // Validate phone number format
        if (request.getPhone() != null && !request.getPhone().matches("^[6-9][0-9]{9}$")) {
            throw new RuntimeException("Phone number must be a valid 10-digit Indian mobile number starting with 6, 7, 8, or 9");
        }
        
        // Check if phone number already exists
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("Phone number already exists");
        }
    }
    
    private String generateRandomPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
} 