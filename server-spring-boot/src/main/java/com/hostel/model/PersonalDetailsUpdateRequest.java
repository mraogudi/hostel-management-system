package com.hostel.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@Document(collection = "personal_details_update_requests")
public class PersonalDetailsUpdateRequest {
    
    @Id
    private String id;
    
    @JsonProperty("student_id")
    private String studentId;
    
    @JsonProperty("student_name")
    private String studentName;
    
    @JsonProperty("student_roll_no")
    private String studentRollNo;
    
    // Updated fields
    private String phone;
    
    @JsonProperty("address_line1")
    private String addressLine1;
    
    @JsonProperty("address_line2")
    private String addressLine2;
    
    private String city;
    private String state;
    
    @JsonProperty("postal_code")
    private String postalCode;
    
    @JsonProperty("guardian_name")
    private String guardianName;
    
    @JsonProperty("guardian_phone")
    private String guardianPhone;
    
    @JsonProperty("guardian_address")
    private String guardianAddress;
    
    private String status; // "pending", "approved", "rejected"
    
    @JsonProperty("warden_comments")
    private String wardenComments;
    
    @JsonProperty("processed_at")
    private LocalDateTime processedAt;
    
    @JsonProperty("processed_by")
    private String processedBy;
    
    @CreatedDate
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public PersonalDetailsUpdateRequest() {
        this.status = "pending";
        this.createdAt = LocalDateTime.now();
    }
    
    public PersonalDetailsUpdateRequest(String studentId, String studentName, String studentRollNo) {
        this();
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentRollNo = studentRollNo;
    }
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    public String getStudentRollNo() { return studentRollNo; }
    public void setStudentRollNo(String studentRollNo) { this.studentRollNo = studentRollNo; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
    
    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    
    public String getGuardianName() { return guardianName; }
    public void setGuardianName(String guardianName) { this.guardianName = guardianName; }
    
    public String getGuardianPhone() { return guardianPhone; }
    public void setGuardianPhone(String guardianPhone) { this.guardianPhone = guardianPhone; }
    
    public String getGuardianAddress() { return guardianAddress; }
    public void setGuardianAddress(String guardianAddress) { this.guardianAddress = guardianAddress; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { 
        this.status = status; 
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getWardenComments() { return wardenComments; }
    public void setWardenComments(String wardenComments) { this.wardenComments = wardenComments; }
    
    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }
    
    public String getProcessedBy() { return processedBy; }
    public void setProcessedBy(String processedBy) { this.processedBy = processedBy; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
} 