package com.hostel.service;

import com.hostel.dto.CreateStudentRequest;
import com.hostel.dto.RoomChangeRequestDto;
import com.hostel.model.User;
import com.hostel.model.Bed;
import com.hostel.model.Room;
import com.hostel.model.RoomChangeRequest;
import com.hostel.model.PersonalDetailsUpdateRequest;
import com.hostel.repository.UserRepository;
import com.hostel.repository.BedRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.repository.RoomChangeRequestRepository;
import com.hostel.repository.PersonalDetailsUpdateRequestRepository;
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
    
    private final UserRepository userRepository;
    
    private final BedRepository bedRepository;
    
    private final RoomRepository roomRepository;
    
    private final RoomChangeRequestRepository roomChangeRequestRepository;
    
    private final PersonalDetailsUpdateRequestRepository personalDetailsUpdateRequestRepository;
    
    private final PasswordEncoder passwordEncoder;
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public StudentService(UserRepository userRepository, BedRepository bedRepository, RoomRepository roomRepository, RoomChangeRequestRepository roomChangeRequestRepository, PersonalDetailsUpdateRequestRepository personalDetailsUpdateRequestRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bedRepository = bedRepository;
        this.roomRepository = roomRepository;
        this.roomChangeRequestRepository = roomChangeRequestRepository;
        this.personalDetailsUpdateRequestRepository = personalDetailsUpdateRequestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String, Object> createStudent(CreateStudentRequest request) {
        // Validation
        validateStudentRequest(request);
        
        // Generate password
        String password = generateRandomPassword();
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
            request.getBranch(),
            request.getAddressLine1(),
            request.getAddressLine2(),
            request.getCity(),
            request.getState(),
            request.getPostalCode(),
            request.getGuardianName(),
            request.getGuardianAddress(),
            request.getGuardianPhone()
        );
        
        User savedStudent = userRepository.save(newStudent);
        
        // Prepare response
        Map<String, Object> studentInfo = getStringObjectMap(savedStudent);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Student created successfully");
        response.put("credentials", Map.of(
            "username", request.getRollNo(),
            "password", password
        ));
        response.put("student", studentInfo);
        
        return response;
    }

    private static Map<String, Object> getStringObjectMap(User savedStudent) {
        Map<String, Object> studentInfo = new HashMap<>();
        studentInfo.put("id", savedStudent.getId());
        studentInfo.put("username", savedStudent.getUsername());
        studentInfo.put("full_name", savedStudent.getFullName());
        studentInfo.put("email", savedStudent.getEmail());
        studentInfo.put("phone", savedStudent.getPhone());
        studentInfo.put("roll_no", savedStudent.getRollNo());
        studentInfo.put("stream", savedStudent.getStream());
        studentInfo.put("branch", savedStudent.getBranch());
        return studentInfo;
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
    
    public List<Map<String, Object>> getAllStudents() {
        System.out.println("=== getAllStudents ===");
        
        // Get all users with role "student"
        List<User> students = userRepository.findByRole("student");
        System.out.println("Found " + students.size() + " students");
        
        return students.stream().map(student -> {
            Map<String, Object> studentMap = new HashMap<>();
            studentMap.put("id", student.getId());
            studentMap.put("full_name", student.getFullName());
            studentMap.put("username", student.getUsername());
            studentMap.put("email", student.getEmail());
            studentMap.put("phone", student.getPhone());
            studentMap.put("date_of_birth", student.getDateOfBirth());
            studentMap.put("gender", student.getGender());
            studentMap.put("aadhaar_id", student.getAadhaarId());
            studentMap.put("roll_no", student.getRollNo());
            studentMap.put("stream", student.getStream());
            studentMap.put("branch", student.getBranch());
            studentMap.put("created_at", student.getCreatedAt());
            studentMap.put("first_login", student.getFirstLogin());
            
            // Get room assignment information
            Optional<Bed> assignedBed = bedRepository.findByStudentId(student.getId());
            if (assignedBed.isPresent()) {
                Optional<Room> room = roomRepository.findById(assignedBed.get().getRoomId());
                if (room.isPresent()) {
                    studentMap.put("room_id", room.get().getId());
                    studentMap.put("room_number", room.get().getRoomNumber());
                    studentMap.put("bed_number", assignedBed.get().getBedNumber());
                } else {
                    studentMap.put("room_id", null);
                    studentMap.put("room_number", null);
                    studentMap.put("bed_number", null);
                }
            } else {
                studentMap.put("room_id", null);
                studentMap.put("room_number", null);
                studentMap.put("bed_number", null);
            }
            
            return studentMap;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> getStudentById(String id) {
        System.out.println("=== getStudentById: " + id + " ===");
        
        Optional<User> studentOptional = userRepository.findById(id);
        if (studentOptional.isEmpty()) {
            throw new RuntimeException("Student not found");
        }
        
        User student = studentOptional.get();
        if (!"student".equals(student.getRole())) {
            throw new RuntimeException("User is not a student");
        }
        
        Map<String, Object> studentMap = new HashMap<>();
        studentMap.put("id", student.getId());
        studentMap.put("full_name", student.getFullName());
        studentMap.put("username", student.getUsername());
        studentMap.put("email", student.getEmail());
        studentMap.put("phone", student.getPhone());
        studentMap.put("date_of_birth", student.getDateOfBirth());
        studentMap.put("gender", student.getGender());
        studentMap.put("aadhaar_id", student.getAadhaarId());
        studentMap.put("roll_no", student.getRollNo());
        studentMap.put("stream", student.getStream());
        studentMap.put("branch", student.getBranch());
        studentMap.put("created_at", student.getCreatedAt());
        studentMap.put("first_login", student.getFirstLogin());
        
        return studentMap;
    }

    public Map<String, Object> updateStudent(String id, Map<String, Object> studentData) {
        System.out.println("=== updateStudent: " + id + " ===");
        
        Optional<User> studentOptional = userRepository.findById(id);
        if (studentOptional.isEmpty()) {
            throw new RuntimeException("Student not found");
        }
        
        User student = studentOptional.get();
        if (!"student".equals(student.getRole())) {
            throw new RuntimeException("User is not a student");
        }
        
        // Update fields if provided
        if (studentData.containsKey("full_name")) {
            student.setFullName((String) studentData.get("full_name"));
        }
        if (studentData.containsKey("email")) {
            student.setEmail((String) studentData.get("email"));
        }
        if (studentData.containsKey("phone")) {
            String phone = (String) studentData.get("phone");
            // Validate phone number
            if (phone != null && !phone.matches("^[6-9][0-9]{9}$")) {
                throw new RuntimeException("Invalid phone number format");
            }
            student.setPhone(phone);
        }
        if (studentData.containsKey("stream")) {
            student.setStream((String) studentData.get("stream"));
        }
        if (studentData.containsKey("branch")) {
            student.setBranch((String) studentData.get("branch"));
        }
        
        User updatedStudent = userRepository.save(student);
        System.out.println("Student updated successfully");
        
        return getStudentById(updatedStudent.getId());
    }

    public void deleteStudent(String id) {
        System.out.println("=== deleteStudent: " + id + " ===");
        
        Optional<User> studentOptional = userRepository.findById(id);
        if (studentOptional.isEmpty()) {
            throw new RuntimeException("Student not found");
        }
        
        User student = studentOptional.get();
        if (!"student".equals(student.getRole())) {
            throw new RuntimeException("User is not a student");
        }
        
        // Remove student from any assigned bed
        Optional<Bed> assignedBed = bedRepository.findByStudentId(id);
        if (assignedBed.isPresent()) {
            Bed bed = assignedBed.get();
            bed.setStudentId(null);
            bed.setStatus("available");
            bedRepository.save(bed);
            System.out.println("Student removed from bed " + bed.getBedNumber());
        }
        
        // Delete any room change requests
        List<RoomChangeRequest> requests = roomChangeRequestRepository.findByStudentId(id);
        if (!requests.isEmpty()) {
            roomChangeRequestRepository.deleteAll(requests);
            System.out.println("Deleted " + requests.size() + " room change requests");
        }
        
        // Delete the student
        userRepository.delete(student);
        System.out.println("Student deleted successfully");
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
    
    private String generateRandomPassword() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
    
    public Map<String, Object> getWardenContact() {
        System.out.println("=== getWardenContact ===");
        
        // Find the warden user
        Optional<User> wardenOptional = userRepository.findByRole("warden").stream().findFirst();
        
        if (wardenOptional.isEmpty()) {
            throw new RuntimeException("Warden contact information not available");
        }
        
        User warden = wardenOptional.get();
        System.out.println("Found warden: " + warden.getFullName());
        
        Map<String, Object> wardenContact = new HashMap<>();
        wardenContact.put("name", warden.getFullName());
        wardenContact.put("email", warden.getEmail());
        wardenContact.put("phone", warden.getPhone());
        wardenContact.put("office_hours", "9:00 AM - 5:00 PM (Monday to Friday)");
        wardenContact.put("emergency_contact", "Available 24/7 for emergencies");
        
        System.out.println("Returning warden contact information");
        return wardenContact;
    }
    
    public Map<String, Object> submitPersonalDetailsUpdateRequest(String userId, Map<String, String> updateRequest) {
        System.out.println("=== submitPersonalDetailsUpdateRequest ===");
        
        // Get student user
        Optional<User> studentOptional = userRepository.findById(userId);
        if (studentOptional.isEmpty()) {
            throw new RuntimeException("Student not found");
        }
        
        User student = studentOptional.get();
        System.out.println("Student found: " + student.getFullName());
        
        // Check if there's already a pending request for this student
        Optional<PersonalDetailsUpdateRequest> existingRequest = 
            personalDetailsUpdateRequestRepository.findByStudentIdAndStatus(userId, "pending");
        
        if (existingRequest.isPresent()) {
            throw new RuntimeException("You already have a pending personal details update request. Please wait for approval or contact the warden.");
        }
        
        // Create new personal details update request
        PersonalDetailsUpdateRequest request = new PersonalDetailsUpdateRequest(
            userId, 
            student.getFullName(),
            student.getRollNo()
        );
        
        // Set the updated fields
        request.setPhone(updateRequest.get("phone"));
        request.setAddressLine1(updateRequest.get("address_line1"));
        request.setAddressLine2(updateRequest.get("address_line2"));
        request.setCity(updateRequest.get("city"));
        request.setState(updateRequest.get("state"));
        request.setPostalCode(updateRequest.get("postal_code"));
        request.setGuardianName(updateRequest.get("guardian_name"));
        request.setGuardianPhone(updateRequest.get("guardian_phone"));
        request.setGuardianAddress(updateRequest.get("guardian_address"));
        
        // Save the request
        PersonalDetailsUpdateRequest savedRequest = personalDetailsUpdateRequestRepository.save(request);
        
        System.out.println("Personal details update request created with ID: " + savedRequest.getId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Personal details update request submitted successfully");
        response.put("request_id", savedRequest.getId());
        response.put("status", "pending");
        
        return response;
    }

    public List<PersonalDetailsUpdateRequest> getAllPersonalDetailsUpdateRequests() {
        System.out.println("=== getAllPersonalDetailsUpdateRequests ===");
        
        List<PersonalDetailsUpdateRequest> requests = personalDetailsUpdateRequestRepository.findAll();
        System.out.println("Found " + requests.size() + " personal details update requests");
        
        return requests;
    }

    public void approvePersonalDetailsUpdateRequest(String requestId, String wardenComments) {
        System.out.println("=== approvePersonalDetailsUpdateRequest ===");
        System.out.println("Request ID: " + requestId);
        System.out.println("Warden Comments: " + wardenComments);

        // Find the request
        Optional<PersonalDetailsUpdateRequest> requestOptional = personalDetailsUpdateRequestRepository.findById(requestId);
        if (requestOptional.isEmpty()) {
            throw new RuntimeException("Personal details update request not found");
        }

        PersonalDetailsUpdateRequest request = requestOptional.get();
        
        if (!"pending".equals(request.getStatus())) {
            throw new RuntimeException("Request has already been processed");
        }

        // Find the student
        Optional<User> studentOptional = userRepository.findById(request.getStudentId());
        if (studentOptional.isEmpty()) {
            throw new RuntimeException("Student not found");
        }

        User student = studentOptional.get();
        System.out.println("Found student: " + student.getFullName());

        // Update student's personal details
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            student.setPhone(request.getPhone());
        }
        if (request.getAddressLine1() != null && !request.getAddressLine1().trim().isEmpty()) {
            student.setAddressLine1(request.getAddressLine1());
        }
        if (request.getAddressLine2() != null) {
            student.setAddressLine2(request.getAddressLine2());
        }
        if (request.getCity() != null && !request.getCity().trim().isEmpty()) {
            student.setCity(request.getCity());
        }
        if (request.getState() != null && !request.getState().trim().isEmpty()) {
            student.setState(request.getState());
        }
        if (request.getPostalCode() != null && !request.getPostalCode().trim().isEmpty()) {
            student.setPostalCode(request.getPostalCode());
        }
        if (request.getGuardianName() != null && !request.getGuardianName().trim().isEmpty()) {
            student.setGuardianName(request.getGuardianName());
        }
        if (request.getGuardianPhone() != null && !request.getGuardianPhone().trim().isEmpty()) {
            student.setGuardianPhone(request.getGuardianPhone());
        }
        if (request.getGuardianAddress() != null && !request.getGuardianAddress().trim().isEmpty()) {
            student.setGuardianAddress(request.getGuardianAddress());
        }

        // Save updated student
        userRepository.save(student);
        System.out.println("Student details updated successfully");

        // Update request status
        request.setStatus("approved");
        request.setProcessedAt(LocalDateTime.now());
        request.setProcessedBy("warden"); // You might want to get actual warden ID
        request.setWardenComments(wardenComments);

        personalDetailsUpdateRequestRepository.save(request);
        System.out.println("Personal details update request approved and processed");
    }

    public void rejectPersonalDetailsUpdateRequest(String requestId, String wardenComments) {
        System.out.println("=== rejectPersonalDetailsUpdateRequest ===");
        System.out.println("Request ID: " + requestId);
        System.out.println("Warden Comments: " + wardenComments);

        // Find the request
        Optional<PersonalDetailsUpdateRequest> requestOptional = personalDetailsUpdateRequestRepository.findById(requestId);
        if (requestOptional.isEmpty()) {
            throw new RuntimeException("Personal details update request not found");
        }

        PersonalDetailsUpdateRequest request = requestOptional.get();
        
        if (!"pending".equals(request.getStatus())) {
            throw new RuntimeException("Request has already been processed");
        }

        // Update request status
        request.setStatus("rejected");
        request.setProcessedAt(LocalDateTime.now());
        request.setProcessedBy("warden"); // You might want to get actual warden ID
        request.setWardenComments(wardenComments);

        personalDetailsUpdateRequestRepository.save(request);
        System.out.println("Personal details update request rejected");
    }
} 