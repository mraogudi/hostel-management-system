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
    
    public Map<String, Object> getRoomDetails(Long roomId) {
        System.out.println("=== getRoomDetails called for roomId: " + roomId + " ===");
        
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        
        if (roomOptional.isEmpty()) {
            System.out.println("ERROR: Room not found for ID: " + roomId);
            throw new RuntimeException("Room not found");
        }
        
        Room room = roomOptional.get();
        System.out.println("Found room: " + room.getRoomNumber() + " (ID: " + roomId + ")");
        System.out.println("Room capacity: " + room.getCapacity());
        
        List<Bed> beds = bedRepository.findByRoomId(roomId);
        System.out.println("Found " + beds.size() + " beds for room " + room.getRoomNumber());
        
        if (beds.isEmpty()) {
            System.out.println("WARNING: No beds found for room " + room.getRoomNumber());
            System.out.println("This indicates a data initialization problem");
        }
        
        List<Map<String, Object>> bedsWithStudents = beds.stream().map(bed -> {
            User student = bed.getStudentId() != null ? 
                userRepository.findById(bed.getStudentId()).orElse(null) : null;
            
            System.out.println("Processing bed " + bed.getBedNumber() + " - Status: '" + bed.getStatus() + "' - Student ID: " + bed.getStudentId());
            
            Map<String, Object> bedMap = new HashMap<>();
            bedMap.put("id", bed.getId());
            bedMap.put("bed_number", bed.getBedNumber());
            bedMap.put("status", bed.getStatus());
            bedMap.put("student_id", bed.getStudentId() != null ? bed.getStudentId() : "");
            bedMap.put("student_name", student != null ? student.getFullName() : null);
            return bedMap;
        }).collect(Collectors.toList());
        
        // Count available beds
        long availableBedsCount = bedsWithStudents.stream()
            .mapToLong(bed -> "available".equals(bed.get("status")) ? 1 : 0)
            .sum();
        
        System.out.println("Total beds in response: " + bedsWithStudents.size());
        System.out.println("Available beds count: " + availableBedsCount);
        System.out.println("=== End getRoomDetails ===");
        
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
    
    public void updateRoomOccupancy(Long roomId) {
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

    public Map<String, Object> getDebugInfo() {
        Map<String, Object> debugInfo = new HashMap<>();
        
        // Get all rooms
        List<Room> rooms = roomRepository.findAll();
        debugInfo.put("total_rooms", rooms.size());
        
        // Get all beds
        List<Bed> allBeds = bedRepository.findAll();
        debugInfo.put("total_beds", allBeds.size());
        
        // Count beds by status
        long availableBeds = allBeds.stream().filter(bed -> "available".equals(bed.getStatus())).count();
        long occupiedBeds = allBeds.stream().filter(bed -> "occupied".equals(bed.getStatus())).count();
        
        debugInfo.put("available_beds", availableBeds);
        debugInfo.put("occupied_beds", occupiedBeds);
        
        // Get specific room R007 info
        Optional<Room> room007 = rooms.stream().filter(r -> "R007".equals(r.getRoomNumber())).findFirst();
        if (room007.isPresent()) {
            Room r007 = room007.get();
            List<Bed> r007beds = bedRepository.findByRoomId(r007.getId());
            
            Map<String, Object> r007Info = new HashMap<>();
            r007Info.put("id", r007.getId());
            r007Info.put("room_number", r007.getRoomNumber());
            r007Info.put("capacity", r007.getCapacity());
            r007Info.put("beds_found", r007beds.size());
            
            // List all beds for R007
            List<Map<String, Object>> bedsList = r007beds.stream().map(bed -> {
                Map<String, Object> bedInfo = new HashMap<>();
                bedInfo.put("bed_number", bed.getBedNumber());
                bedInfo.put("status", bed.getStatus());
                bedInfo.put("student_id", bed.getStudentId());
                return bedInfo;
            }).collect(Collectors.toList());
            
            r007Info.put("beds", bedsList);
            debugInfo.put("room_R007", r007Info);
        } else {
            debugInfo.put("room_R007", "NOT FOUND");
        }
        
        return debugInfo;
    }
} 