package com.hostel.service;

import com.hostel.dto.LoginRequest;
import com.hostel.dto.LoginResponse;
import com.hostel.dto.UserDto;
import com.hostel.model.User;
import com.hostel.repository.UserRepository;
import com.hostel.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }
        
        User user = userOptional.get();
String decPwd = passwordEncoder.encode("password123");
System.out.println("Decode Pwd :: " + decPwd);
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());
        UserDto userDto = convertToUserDto(user);
        
        return new LoginResponse(token, userDto);
    }
    
    public UserDto getUserProfile(String userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOptional.get();
        return convertToUserDto(user);
    }
    
    public void changePassword(String userId, String currentPassword, String newPassword) {
        if (newPassword.length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters long");
        }
        
        Optional<User> userOptional = userRepository.findById(userId);
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        
        User user = userOptional.get();
        
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Update password and set firstLogin to false
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFirstLogin(false);
        userRepository.save(user);
    }
    
    private UserDto convertToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setRole(user.getRole());
        userDto.setFullName(user.getFullName());
        userDto.setEmail(user.getEmail());
        userDto.setPhone(user.getPhone());
        userDto.setCreatedAt(user.getCreatedAt());
        userDto.setFirstLogin(user.getFirstLogin());
        
        // Include student-specific fields if user is a student
        if ("student".equals(user.getRole())) {
            userDto.setDateOfBirth(user.getDateOfBirth());
            userDto.setGender(user.getGender());
            userDto.setAadhaarId(user.getAadhaarId());
            userDto.setRollNo(user.getRollNo());
            userDto.setStream(user.getStream());
            userDto.setBranch(user.getBranch());
            
            // Include address fields
            userDto.setAddressLine1(user.getAddressLine1());
            userDto.setAddressLine2(user.getAddressLine2());
            userDto.setCity(user.getCity());
            userDto.setState(user.getState());
            userDto.setPostalCode(user.getPostalCode());
            
            // Include guardian fields
            userDto.setGuardianName(user.getGuardianName());
            userDto.setGuardianAddress(user.getGuardianAddress());
            userDto.setGuardianPhone(user.getGuardianPhone());
        }
        
        return userDto;
    }
} 