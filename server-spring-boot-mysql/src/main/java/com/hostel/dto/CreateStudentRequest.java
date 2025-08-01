package com.hostel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Past;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

public class CreateStudentRequest {
    
    @NotBlank(message = "Full name is required")
    @JsonProperty("full_name")
    private String fullName;
    
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Phone number must be a valid 10-digit Indian mobile number starting with 6, 7, 8, or 9")
    private String phone;
    
    // New student-specific fields
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @JsonProperty("date_of_birth")
    private LocalDate dateOfBirth;
    
    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "^(Male|Female|Other)$", message = "Gender must be Male, Female, or Other")
    private String gender;
    
    @NotBlank(message = "Aadhaar ID is required")
    @Pattern(regexp = "^[0-9]{12}$", message = "Aadhaar ID must be 12 digits")
    @JsonProperty("aadhaar_id")
    private String aadhaarId;
    
    @NotBlank(message = "Roll number is required")
    @JsonProperty("roll_no")
    private String rollNo;
    
    @NotBlank(message = "Stream is required")
    private String stream;
    
    @NotBlank(message = "Branch is required")
    private String branch;
    
    // Address fields
    @NotBlank(message = "Address line 1 is required")
    @JsonProperty("address_line1")
    private String addressLine1;
    
    @JsonProperty("address_line2")
    private String addressLine2; // Optional
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "State is required")
    private String state;
    
    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "Postal code must be 6 digits")
    @JsonProperty("postal_code")
    private String postalCode;
    
    // Guardian fields
    @NotBlank(message = "Guardian name is required")
    @JsonProperty("guardian_name")
    private String guardianName;
    
    @NotBlank(message = "Guardian address is required")
    @JsonProperty("guardian_address")
    private String guardianAddress;
    
    @NotBlank(message = "Guardian phone number is required")
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Guardian phone must be a valid 10-digit Indian mobile number starting with 6, 7, 8, or 9")
    @JsonProperty("guardian_phone")
    private String guardianPhone;
    
    // Constructors
    public CreateStudentRequest() {}
    
    public CreateStudentRequest(String fullName, String email, String phone,
                               LocalDate dateOfBirth, String gender, String aadhaarId, 
                               String rollNo, String stream, String branch,
                               String addressLine1, String addressLine2, String city, String state, String postalCode,
                               String guardianName, String guardianAddress, String guardianPhone) {
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
    
    // Getters and Setters - removed username getter/setter
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