package com.hostel.repository;

import com.hostel.model.RoomChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomChangeRequestRepository extends JpaRepository<RoomChangeRequest, Long> {
    
    List<RoomChangeRequest> findByStudentId(Long studentId);
    
    List<RoomChangeRequest> findByStatus(String status);
    
    List<RoomChangeRequest> findByStudentIdAndStatus(Long studentId, String status);
    
    List<RoomChangeRequest> findAllByOrderByRequestedAtDesc();
} 