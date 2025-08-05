package com.hostel.repository;

import com.hostel.model.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BedRepository extends JpaRepository<Bed, Long> {
    
    List<Bed> findByRoomId(Long roomId);
    
    List<Bed> findByStatus(String status);
    
    Optional<Bed> findByStudentId(Long studentId);
    
    Optional<Bed> findByRoomIdAndBedNumber(Long roomId, Integer bedNumber);
    
    long countByRoomIdAndStatus(Long roomId, String status);
    
    // Count methods for statistics
    long countByStatus(String status);
} 