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
import java.time.LocalDateTime;
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
    
    public void assignRoom(String rollNumber, String roomId, Integer bedNumber) {
        // First, find the student by roll number
        Optional<User> studentOptional = userRepository.findByRollNo(rollNumber);
        
        if (studentOptional.isEmpty()) {
            throw new RuntimeException("Student not found with roll number: " + rollNumber);
        }
        
        User student = studentOptional.get();
        String studentId = student.getId(); // Get the actual MongoDB ObjectId
        
        // Check if student is already assigned to a room
        Optional<Bed> existingBedOptional = bedRepository.findByStudentId(studentId);
        if (existingBedOptional.isPresent()) {
            throw new RuntimeException("Student " + rollNumber + " is already assigned to a room");
        }
        
        // Find the requested bed
        Optional<Bed> bedOptional = bedRepository.findByRoomIdAndBedNumber(roomId, bedNumber);
        
        if (bedOptional.isEmpty() || !"available".equals(bedOptional.get().getStatus())) {
            throw new RuntimeException("Bed not available");
        }
        
        Bed bed = bedOptional.get();
        bed.setStudentId(studentId); // Use the actual student ObjectId
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
        System.out.println("=== submitRoomChangeRequest ===");
        System.out.println("User ID: " + userId);
        System.out.println("Requested Room ID: " + requestDto.getRequestedRoomId());
        System.out.println("Requested Bed Number: " + requestDto.getRequestedBedNumber());
        System.out.println("Reason: " + requestDto.getReason());
        
        // Get current room of student
        Optional<Bed> currentBedOptional = bedRepository.findByStudentId(userId);
        String currentRoomId = currentBedOptional.map(Bed::getRoomId).orElse(null);
        System.out.println("Current Room ID: " + currentRoomId);
        
        // Validate that the requested bed is available
        Optional<Bed> requestedBedOptional = bedRepository.findByRoomIdAndBedNumber(
            requestDto.getRequestedRoomId(), 
            requestDto.getRequestedBedNumber()
        );
        
        if (requestedBedOptional.isEmpty()) {
            System.out.println("ERROR: Requested bed does not exist");
            throw new RuntimeException("Requested bed does not exist");
        }
        
        Bed requestedBed = requestedBedOptional.get();
        System.out.println("Requested bed status: " + requestedBed.getStatus());
        
        if (!"available".equals(requestedBed.getStatus())) {
            System.out.println("ERROR: Requested bed is not available");
            throw new RuntimeException("Requested bed is not available");
        }
        
        RoomChangeRequest roomChangeRequest = new RoomChangeRequest(
            userId,
            currentRoomId,
            requestDto.getRequestedRoomId(),
            requestDto.getRequestedBedNumber(),
            requestDto.getReason()
        );
        
        RoomChangeRequest savedRequest = roomChangeRequestRepository.save(roomChangeRequest);
        System.out.println("Room change request saved with ID: " + savedRequest.getId());
        System.out.println("=== End submitRoomChangeRequest ===");
    }
    
    public void approveRoomChangeRequest(String requestId) {
        System.out.println("=== approveRoomChangeRequest ===");
        System.out.println("Request ID: " + requestId);
        
        // Find the room change request
        Optional<RoomChangeRequest> requestOptional = roomChangeRequestRepository.findById(requestId);
        if (requestOptional.isEmpty()) {
            throw new RuntimeException("Room change request not found");
        }
        
        RoomChangeRequest request = requestOptional.get();
        System.out.println("Student ID: " + request.getStudentId());
        System.out.println("Requested Room ID: " + request.getRequestedRoomId());
        System.out.println("Requested Bed Number: " + request.getRequestedBedNumber());
        
        if (!"pending".equals(request.getStatus())) {
            throw new RuntimeException("Request has already been processed");
        }
        
        // Check if the requested bed is still available
        Optional<Bed> requestedBedOptional = bedRepository.findByRoomIdAndBedNumber(
            request.getRequestedRoomId(), 
            request.getRequestedBedNumber()
        );
        
        if (requestedBedOptional.isEmpty()) {
            throw new RuntimeException("Requested bed no longer exists");
        }
        
        Bed requestedBed = requestedBedOptional.get();
        if (!"available".equals(requestedBed.getStatus())) {
            throw new RuntimeException("Requested bed is no longer available");
        }
        
        // Get student's current bed (if any)
        Optional<Bed> currentBedOptional = bedRepository.findByStudentId(request.getStudentId());
        
        // Free up current bed if student has one
        if (currentBedOptional.isPresent()) {
            Bed currentBed = currentBedOptional.get();
            System.out.println("Freeing current bed: Room " + currentBed.getRoomId() + ", Bed " + currentBed.getBedNumber());
            currentBed.setStudentId(null);
            currentBed.setStatus("available");
            bedRepository.save(currentBed);
        }
        
        // Assign the new bed to the student
        System.out.println("Assigning new bed: Room " + requestedBed.getRoomId() + ", Bed " + requestedBed.getBedNumber());
        requestedBed.setStudentId(request.getStudentId());
        requestedBed.setStatus("occupied");
        bedRepository.save(requestedBed);
        
        // Update the room change request status
        request.setStatus("approved");
        request.setProcessedAt(LocalDateTime.now());
        request.setProcessedBy("warden"); // In a real system, this would be the current user's ID
        roomChangeRequestRepository.save(request);
        
        System.out.println("Room change request approved successfully");
        System.out.println("=== End approveRoomChangeRequest ===");
    }
    
    public void rejectRoomChangeRequest(String requestId) {
        System.out.println("=== rejectRoomChangeRequest ===");
        System.out.println("Request ID: " + requestId);
        
        // Find the room change request
        Optional<RoomChangeRequest> requestOptional = roomChangeRequestRepository.findById(requestId);
        if (requestOptional.isEmpty()) {
            throw new RuntimeException("Room change request not found");
        }
        
        RoomChangeRequest request = requestOptional.get();
        System.out.println("Student ID: " + request.getStudentId());
        
        if (!"pending".equals(request.getStatus())) {
            throw new RuntimeException("Request has already been processed");
        }
        
        // Update the room change request status
        request.setStatus("rejected");
        request.setProcessedAt(LocalDateTime.now());
        request.setProcessedBy("warden"); // In a real system, this would be the current user's ID
        roomChangeRequestRepository.save(request);
        
        System.out.println("Room change request rejected successfully");
        System.out.println("=== End rejectRoomChangeRequest ===");
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