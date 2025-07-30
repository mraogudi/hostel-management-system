package com.hostel.repository;

import com.hostel.model.RoomChangeRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomChangeRequestRepository extends MongoRepository<RoomChangeRequest, String> {
    
    List<RoomChangeRequest> findByStudentId(String studentId);
    
    List<RoomChangeRequest> findByStatus(String status);
    
    List<RoomChangeRequest> findByStudentIdAndStatus(String studentId, String status);
    
    List<RoomChangeRequest> findAllByOrderByRequestedAtDesc();
} 