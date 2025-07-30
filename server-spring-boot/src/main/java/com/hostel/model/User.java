package com.hostel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "users")
public class User {
    
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String username;
    
    @JsonIgnore
    private String password;
    
    private String role; // "student" or "warden"
    
    @JsonProperty("full_name")
    private String fullName;
    
    private String email;
    
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
    private String phone;
    
    // New student-specific fields
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;
    
    private String gender;
    
    @JsonProperty("aadhaar_id")
    @Indexed(unique = true, sparse = true) // sparse allows null values, unique for non-null
    private String aadhaarId;
    
    @JsonProperty("roll_no")
    @Indexed(unique = true, sparse = true) // sparse allows null values, unique for non-null
    private String rollNo;
    
    private String stream;
    
    private String branch;
    
    @CreatedDate
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    // Track if user needs to change password (for first-time login)
    @JsonProperty("first_login")
    private Boolean firstLogin = true; // Default to true for new users
    
    // Constructors
    public User() {}
    
    public User(String username, String password, String role, String fullName, String email, String phone) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }
    
    // Constructor for students with all fields
    public User(String username, String password, String role, String fullName, String email, String phone,
                LocalDate dateOfBirth, String gender, String aadhaarId, String rollNo, String stream, String branch) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.aadhaarId = aadhaarId;
        this.rollNo = rollNo;
        this.stream = stream;
        this.branch = branch;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getAadhaarId() {
        return aadhaarId;
    }
    
    public void setAadhaarId(String aadhaarId) {
        this.aadhaarId = aadhaarId;
    }
    
    public String getRollNo() {
        return rollNo;
    }
    
    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }
    
    public String getStream() {
        return stream;
    }
    
    public void setStream(String stream) {
        this.stream = stream;
    }
    
    public String getBranch() {
        return branch;
    }
    
    public void setBranch(String branch) {
        this.branch = branch;
    }
    
    public Boolean getFirstLogin() {
        return firstLogin;
    }
    
    public void setFirstLogin(Boolean firstLogin) {
        this.firstLogin = firstLogin;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
} 