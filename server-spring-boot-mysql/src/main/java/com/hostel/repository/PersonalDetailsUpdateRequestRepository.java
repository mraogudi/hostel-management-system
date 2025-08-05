package com.hostel.repository;

import com.hostel.model.PersonalDetailsUpdateRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalDetailsUpdateRequestRepository extends JpaRepository<PersonalDetailsUpdateRequest, Long> {
    
    List<PersonalDetailsUpdateRequest> findByStatus(String status);
    
    List<PersonalDetailsUpdateRequest> findByStudentId(Long studentId);
    
    Optional<PersonalDetailsUpdateRequest> findByStudentIdAndStatus(Long studentId, String status);
    
    List<PersonalDetailsUpdateRequest> findAllByOrderByCreatedAtDesc();
} 