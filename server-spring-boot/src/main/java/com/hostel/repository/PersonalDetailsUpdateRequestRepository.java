package com.hostel.repository;

import com.hostel.model.PersonalDetailsUpdateRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalDetailsUpdateRequestRepository extends MongoRepository<PersonalDetailsUpdateRequest, String> {
    
    List<PersonalDetailsUpdateRequest> findByStatus(String status);
    
    List<PersonalDetailsUpdateRequest> findByStudentId(String studentId);
    
    Optional<PersonalDetailsUpdateRequest> findByStudentIdAndStatus(String studentId, String status);
    
    List<PersonalDetailsUpdateRequest> findAllByOrderByCreatedAtDesc();
} 