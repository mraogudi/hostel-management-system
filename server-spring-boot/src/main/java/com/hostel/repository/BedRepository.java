package com.hostel.repository;

import com.hostel.model.Bed;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BedRepository extends MongoRepository<Bed, String> {
    
    List<Bed> findByRoomId(String roomId);
    
    List<Bed> findByStatus(String status);
    
    Optional<Bed> findByStudentId(String studentId);
    
    Optional<Bed> findByRoomIdAndBedNumber(String roomId, Integer bedNumber);
    
    long countByRoomIdAndStatus(String roomId, String status);
} 