package com.hostel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class UserDto {
    
    private Long id;
    private String username;
    private String role;
    
    @JsonProperty("full_name")
    private String fullName;
    
    private String email;
    private String phone;
    
    // New student-specific fields
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;
    
    private String gender;
    
    @JsonProperty("aadhaar_id")
    private String aadhaarId;
    
    @JsonProperty("roll_no")
    private String rollNo;
    
    private String stream;
    private String branch;
    
    // Address fields
    @JsonProperty("address_line1")
    private String addressLine1;
    
    @JsonProperty("address_line2")
    private String addressLine2;
    
    private String city;
    private String state;
    
    @JsonProperty("postal_code")
    private String postalCode;
    
    // Guardian fields
    @JsonProperty("guardian_name")
    private String guardianName;
    
    @JsonProperty("guardian_address")
    private String guardianAddress;
    
    @JsonProperty("guardian_phone")
    private String guardianPhone;
    
    @JsonProperty("first_login")
    private Boolean firstLogin;
    
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    // Constructors
    public UserDto() {}
    
    public UserDto(Long id, String username, String role, String fullName, String email) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.fullName = fullName;
        this.email = email;
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