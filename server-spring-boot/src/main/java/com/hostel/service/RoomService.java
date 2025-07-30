package com.hostel.service;

import com.hostel.model.Room;
import com.hostel.model.Bed;
import com.hostel.model.User;
import com.hostel.repository.RoomRepository;
import com.hostel.repository.BedRepository;
import com.hostel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomService {
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private BedRepository bedRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public List<Map<String, Object>> getAllRoomsWithStats() {
        List<Room> rooms = roomRepository.findAll();
        
        return rooms.stream().map(room -> {
            long occupiedBeds = bedRepository.countByRoomIdAndStatus(room.getId(), "occupied");
            long availableBeds = bedRepository.countByRoomIdAndStatus(room.getId(), "available");
            
            Map<String, Object> roomMap = new HashMap<>();
            roomMap.put("id", room.getId());
            roomMap.put("room_number", room.getRoomNumber());
            roomMap.put("floor", room.getFloor());
            roomMap.put("capacity", room.getCapacity());
            roomMap.put("room_type", room.getRoomType());
            roomMap.put("occupied_beds", occupiedBeds);
            roomMap.put("available_beds", availableBeds);
            roomMap.put("created_at", room.getCreatedAt());
            return roomMap;
        }).collect(Collectors.toList());
    }
    
    public Map<String, Object> getRoomDetails(String roomId) {
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        
        if (roomOptional.isEmpty()) {
            throw new RuntimeException("Room not found");
        }
        
        Room room = roomOptional.get();
        List<Bed> beds = bedRepository.findByRoomId(roomId);
        
        List<Map<String, Object>> bedsWithStudents = beds.stream().map(bed -> {
            User student = bed.getStudentId() != null ? 
                userRepository.findById(bed.getStudentId()).orElse(null) : null;
            
            Map<String, Object> bedMap = new HashMap<>();
            bedMap.put("id", bed.getId());
            bedMap.put("bed_number", bed.getBedNumber());
            bedMap.put("status", bed.getStatus());
            bedMap.put("student_id", bed.getStudentId() != null ? bed.getStudentId() : "");
            bedMap.put("student_name", student != null ? student.getFullName() : null);
            return bedMap;
        }).collect(Collectors.toList());
        
        Map<String, Object> roomDetails = new HashMap<>();
        roomDetails.put("id", room.getId());
        roomDetails.put("room_number", room.getRoomNumber());
        roomDetails.put("floor", room.getFloor());
        roomDetails.put("capacity", room.getCapacity());
        roomDetails.put("room_type", room.getRoomType());
        roomDetails.put("created_at", room.getCreatedAt());
        roomDetails.put("beds", bedsWithStudents);
        
        return roomDetails;
    }
    
    public Room createRoom(String roomNumber, Integer floor, Integer capacity, String roomType) {
        Room room = new Room(roomNumber, floor, capacity, roomType);
        Room savedRoom = roomRepository.save(room);
        
        // Create beds for the room
        for (int i = 1; i <= capacity; i++) {
            Bed bed = new Bed(savedRoom.getId(), i);
            bedRepository.save(bed);
        }
        
        return savedRoom;
    }
    
    public void updateRoomOccupancy(String roomId) {
        Room room = roomRepository.findById(roomId).orElse(null);
        if (room != null) {
            long occupiedCount = bedRepository.countByRoomIdAndStatus(roomId, "occupied");
            room.setOccupiedBeds((int) occupiedCount);
            roomRepository.save(room);
        }
    }
    
    public List<Room> getAvailableRooms() {
        List<Room> allRooms = roomRepository.findAll();
        return allRooms.stream()
            .filter(room -> {
                long availableBeds = bedRepository.countByRoomIdAndStatus(room.getId(), "available");
                return availableBeds > 0;
            })
            .collect(Collectors.toList());
    }
    
    public Map<String, Object> getRoomStatistics() {
        List<Room> rooms = roomRepository.findAll();
        
        int totalRooms = rooms.size();
        long occupiedRooms = rooms.stream()
            .filter(room -> room.getOccupiedBeds() != null && room.getOccupiedBeds() > 0)
            .count();
        int totalBeds = rooms.stream()
            .mapToInt(room -> room.getCapacity() != null ? room.getCapacity() : 0)
            .sum();
        int occupiedBeds = rooms.stream()
            .mapToInt(room -> room.getOccupiedBeds() != null ? room.getOccupiedBeds() : 0)
            .sum();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRooms", totalRooms);
        stats.put("occupiedRooms", occupiedRooms);
        stats.put("totalBeds", totalBeds);
        stats.put("occupiedBeds", occupiedBeds);
        stats.put("availableBeds", totalBeds - occupiedBeds);
        
        return stats;
    }
} 