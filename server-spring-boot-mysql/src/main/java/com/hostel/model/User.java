package com.hostel.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @JsonIgnore
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String role; // "student" or "warden"
    
    @JsonProperty("full_name")
    @Column(name = "full_name")
    private String fullName;
    
    private String email;
    
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Phone number must be a valid 10-digit Indian mobile number")
    private String phone;
    
    // New student-specific fields
    @JsonProperty("date_of_birth")
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    private String gender;
    
    @JsonProperty("aadhaar_id")
    @Column(name = "aadhaar_id", unique = true)
    private String aadhaarId;
    
    @JsonProperty("roll_no")
    @Column(name = "roll_no", unique = true)
    private String rollNo;
    
    private String stream;
    
    private String branch;
    
    // Address fields
    @JsonProperty("address_line1")
    @Column(name = "address_line1")
    private String addressLine1;
    
    @JsonProperty("address_line2")
    @Column(name = "address_line2")
    private String addressLine2;
    
    private String city;
    
    private String state;
    
    @JsonProperty("postal_code")
    @Column(name = "postal_code")
    private String postalCode;
    
    // Guardian fields
    @JsonProperty("guardian_name")
    @Column(name = "guardian_name")
    private String guardianName;
    
    @JsonProperty("guardian_address")
    @Column(name = "guardian_address")
    private String guardianAddress;
    
    @JsonProperty("guardian_phone")
    @Column(name = "guardian_phone")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Guardian phone must be a valid 10-digit Indian mobile number")
    private String guardianPhone;
    
    @CreationTimestamp
    @JsonProperty("created_at")
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    // Track if user needs to change password (for first-time login)
    @JsonProperty("first_login")
    @Column(name = "first_login")
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
    
    // Constructor for students with all fields including address and guardian
    public User(String username, String password, String role, String fullName, String email, String phone,
                LocalDate dateOfBirth, String gender, String aadhaarId, String rollNo, String stream, String branch,
                String addressLine1, String addressLine2, String city, String state, String postalCode,
                String guardianName, String guardianAddress, String guardianPhone) {
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
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.guardianName = guardianName;
        this.guardianAddress = guardianAddress;
        this.guardianPhone = guardianPhone;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
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
    
    // Address field getters and setters
    public String getAddressLine1() {
        return addressLine1;
    }
    
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }
    
    public String getAddressLine2() {
        return addressLine2;
    }
    
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    // Guardian field getters and setters
    public String getGuardianName() {
        return guardianName;
    }
    
    public void setGuardianName(String guardianName) {
        this.guardianName = guardianName;
    }
    
    public String getGuardianAddress() {
        return guardianAddress;
    }
    
    public void setGuardianAddress(String guardianAddress) {
        this.guardianAddress = guardianAddress;
    }
    
    public String getGuardianPhone() {
        return guardianPhone;
    }
    
    public void setGuardianPhone(String guardianPhone) {
        this.guardianPhone = guardianPhone;
    }
} 