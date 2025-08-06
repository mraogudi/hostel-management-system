-- MySQL initialization script for Hostel Management System
-- This script creates the necessary tables and inserts sample data

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS hostel_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hostel_management;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('warden', 'student') NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    guardian_name VARCHAR(100),
    guardian_phone VARCHAR(20),
    guardian_address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Rooms table
CREATE TABLE IF NOT EXISTS rooms (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(20) NOT NULL UNIQUE,
    floor INT NOT NULL,
    capacity INT NOT NULL DEFAULT 3,
    occupied_beds INT NOT NULL DEFAULT 0,
    room_type VARCHAR(50) DEFAULT 'standard',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_room_number (room_number),
    INDEX idx_floor (floor)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Beds table
CREATE TABLE IF NOT EXISTS beds (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    bed_number INT NOT NULL,
    student_id BIGINT NULL,
    status ENUM('available', 'occupied', 'maintenance') DEFAULT 'available',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY unique_room_bed (room_id, bed_number),
    INDEX idx_room_id (room_id),
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Food Menu table
CREATE TABLE IF NOT EXISTS food_menu (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    meal_type ENUM('breakfast', 'lunch', 'dinner') NOT NULL,
    day_of_week ENUM('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday') NOT NULL,
    items TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_meal_day (meal_type, day_of_week)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Room Change Requests table
CREATE TABLE IF NOT EXISTS room_change_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    current_room_id BIGINT,
    current_bed_number INT,
    requested_room_id BIGINT NOT NULL,
    requested_bed_number INT NOT NULL,
    reason TEXT,
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    processed_by BIGINT NULL,
    comments TEXT,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (current_room_id) REFERENCES rooms(id) ON DELETE SET NULL,
    FOREIGN KEY (requested_room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Personal Details Update Requests table
CREATE TABLE IF NOT EXISTS personal_details_update_requests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    phone VARCHAR(20),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    guardian_name VARCHAR(100),
    guardian_phone VARCHAR(20),
    guardian_address TEXT,
    status ENUM('pending', 'approved', 'rejected') DEFAULT 'pending',
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    processed_by BIGINT NULL,
    comments TEXT,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_student_id (student_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insert sample data
-- Default warden user (password: warden123)
INSERT IGNORE INTO users (username, password, role, full_name, email, phone) VALUES
('warden', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'warden', 'Hostel Warden', 'warden@hostel.edu', '9876543210');

-- Sample rooms
INSERT IGNORE INTO rooms (room_number, floor, capacity, occupied_beds, room_type) VALUES
('R001', 1, 3, 0, 'standard'),
('R002', 1, 3, 0, 'standard'),
('R003', 1, 3, 0, 'standard'),
('R004', 1, 3, 0, 'standard'),
('R005', 2, 3, 0, 'standard'),
('R006', 2, 3, 0, 'standard'),
('R007', 2, 3, 0, 'standard'),
('R008', 2, 3, 0, 'standard'),
('R009', 3, 3, 0, 'standard'),
('R010', 3, 3, 0, 'standard');

-- Sample beds (3 beds per room)
INSERT IGNORE INTO beds (room_id, bed_number, status) VALUES
(1, 1, 'available'), (1, 2, 'available'), (1, 3, 'available'),
(2, 1, 'available'), (2, 2, 'available'), (2, 3, 'available'),
(3, 1, 'available'), (3, 2, 'available'), (3, 3, 'available'),
(4, 1, 'available'), (4, 2, 'available'), (4, 3, 'available'),
(5, 1, 'available'), (5, 2, 'available'), (5, 3, 'available'),
(6, 1, 'available'), (6, 2, 'available'), (6, 3, 'available'),
(7, 1, 'available'), (7, 2, 'available'), (7, 3, 'available'),
(8, 1, 'available'), (8, 2, 'available'), (8, 3, 'available'),
(9, 1, 'available'), (9, 2, 'available'), (9, 3, 'available'),
(10, 1, 'available'), (10, 2, 'available'), (10, 3, 'available');

-- Sample food menu
INSERT IGNORE INTO food_menu (meal_type, day_of_week, items) VALUES
('breakfast', 'Monday', 'Bread, Butter, Jam, Tea/Coffee, Boiled Eggs'),
('lunch', 'Monday', 'Rice, Dal, Vegetable Curry, Chapati, Pickle'),
('dinner', 'Monday', 'Rice, Sambar, Dry Vegetable, Chapati, Curd'),
('breakfast', 'Tuesday', 'Poha, Tea/Coffee, Banana'),
('lunch', 'Tuesday', 'Rice, Rasam, Vegetable Curry, Chapati, Papad'),
('dinner', 'Tuesday', 'Rice, Dal, Mixed Vegetable, Chapati, Pickle'),
('breakfast', 'Wednesday', 'Idli, Sambar, Chutney, Tea/Coffee'),
('lunch', 'Wednesday', 'Rice, Curd, Vegetable, Chapati, Pickle'),
('dinner', 'Wednesday', 'Rice, Dal, Fry, Chapati, Salad'),
('breakfast', 'Thursday', 'Upma, Tea/Coffee, Biscuits'),
('lunch', 'Thursday', 'Rice, Sambar, Vegetable, Chapati, Pickle'),
('dinner', 'Thursday', 'Rice, Dal, Curry, Chapati, Salad'),
('breakfast', 'Friday', 'Paratha, Curd, Pickle, Tea/Coffee'),
('lunch', 'Friday', 'Rice, Rasam, Fry, Chapati, Papad'),
('dinner', 'Friday', 'Rice, Dal, Vegetable, Chapati, Curd'),
('breakfast', 'Saturday', 'Dosa, Sambar, Chutney, Tea/Coffee'),
('lunch', 'Saturday', 'Rice, Curd, Curry, Chapati, Pickle'),
('dinner', 'Saturday', 'Rice, Dal, Mixed Vegetable, Chapati, Salad'),
('breakfast', 'Sunday', 'Puri, Sabzi, Tea/Coffee, Sweets'),
('lunch', 'Sunday', 'Rice, Dal, Special Curry, Chapati, Pickle'),
('dinner', 'Sunday', 'Rice, Sambar, Vegetable, Chapati, Curd');

SET FOREIGN_KEY_CHECKS = 1;

-- Display completion message
SELECT 'MySQL database initialized successfully!' as message;
SELECT 'Default login: username=warden, password=warden123' as credentials; 