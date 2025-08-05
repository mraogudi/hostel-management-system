package com.hostel.repository;

import com.hostel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    List<User> findByRole(String role);
    
    boolean existsByUsername(String username);
    
    // New methods for student-specific fields
    Optional<User> findByAadhaarId(String aadhaarId);
    
    Optional<User> findByRollNo(String rollNo);
    
    Optional<User> findByPhone(String phone);
    
    boolean existsByAadhaarId(String aadhaarId);
    
    boolean existsByRollNo(String rollNo);
    
    boolean existsByPhone(String phone);
    
    // Count methods for statistics
    long countByRole(String role);
} 