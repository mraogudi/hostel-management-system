# Hostel Management System - Backend (Spring Boot)

A robust Spring Boot backend API for the hostel management system with JWT authentication, MongoDB integration, and layered architecture following enterprise patterns.

## 🚀 Quick Start

### Prerequisites
- **Java** (v17 or higher)
- **Maven** (v3.6 or higher)
- **MongoDB** (v4.4 or higher)

### Installation & Setup

```bash
# Navigate to Spring Boot directory
cd server-spring-boot

# Install dependencies and build
mvn clean install

# Start the server
mvn spring-boot:run

# The backend will start on http://localhost:8080
```

## 🏗️ Architecture

### Technology Stack
- **Framework**: Spring Boot 3.2
- **Security**: Spring Security with JWT
- **Database**: MongoDB with Spring Data
- **Architecture**: Layered architecture with Service Layer separation
- **Validation**: Bean Validation (JSR-303)
- **Password Encryption**: BCrypt

### Project Structure
```
server-spring-boot/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── hostel/
│   │   │           ├── HostelManagementApplication.java
│   │   │           ├── controller/
│   │   │           │   ├── AuthController.java
│   │   │           │   ├── StudentController.java
│   │   │           │   ├── WardenController.java
│   │   │           │   ├── RoomController.java
│   │   │           │   └── FoodMenuController.java
│   │   │           ├── service/
│   │   │           │   ├── AuthService.java
│   │   │           │   ├── StudentService.java
│   │   │           │   ├── RoomService.java
│   │   │           │   └── FoodMenuService.java
│   │   │           ├── repository/
│   │   │           │   ├── UserRepository.java
│   │   │           │   ├── RoomRepository.java
│   │   │           │   ├── BedRepository.java
│   │   │           │   ├── RoomChangeRequestRepository.java
│   │   │           │   └── FoodMenuRepository.java
│   │   │           ├── model/
│   │   │           │   ├── User.java
│   │   │           │   ├── Room.java
│   │   │           │   ├── Bed.java
│   │   │           │   ├── RoomChangeRequest.java
│   │   │           │   └── FoodMenu.java
│   │   │           ├── dto/
│   │   │           │   ├── LoginRequest.java
│   │   │           │   ├── LoginResponse.java
│   │   │           │   ├── StudentRequest.java
│   │   │           │   └── RoomAssignmentRequest.java
│   │   │           ├── config/
│   │   │           │   ├── JwtConfig.java
│   │   │           │   ├── SecurityConfig.java
│   │   │           │   └── MongoConfig.java
│   │   │           └── util/
│   │   │               ├── JwtUtil.java
│   │   │               └── PasswordUtil.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-dev.yml
│   └── test/
├── pom.xml
└── README.md
```

## 🏗️ Layered Architecture

The application follows a **layered architecture** pattern with clear separation of concerns:

### Controller Layer (`@RestController`)
- Handles HTTP requests and responses
- Input validation and error handling
- Maps endpoints to service methods
- Maintains clean, focused controllers

### Service Layer (`@Service`)
- **AuthService**: User authentication, password management, profile operations
- **StudentService**: Student lifecycle management, room assignments, change requests
- **RoomService**: Room management, occupancy tracking, statistics
- **FoodMenuService**: Food menu operations and data management

### Repository Layer (`@Repository`)
- Data access and database operations
- Spring Data MongoDB integration
- Custom query methods

### Model Layer (`@Document`)
- Entity definitions with MongoDB mapping
- Field validation annotations
- JSON serialization configuration

## 📊 Database Models

### User Entity
```java
@Document(collection = "users")
public class User {
    @Id
    private String id;
    
    @NotBlank
    @Indexed(unique = true)
    private String username;
    
    @NotBlank
    private String password;
    
    @NotBlank
    private String role; // "student" or "warden"
    
    @NotBlank
    private String fullName;
    
    @Email
    private String email;
    
    @Pattern(regexp = "^[6-9]\\d{9}$")
    private String phone;
    
    private LocalDate dateOfBirth;
    private String gender;
    private String rollNumber;
    private String stream;
    private String branch;
    
    // Address fields
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String postalCode;
    
    // Guardian fields
    private String guardianName;
    private String guardianPhone;
    private String guardianAddress;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors, getters, setters
}
```

### Room Entity
```java
@Document(collection = "rooms")
public class Room {
    @Id
    private String id;
    
    @NotBlank
    @Indexed(unique = true)
    private String roomNumber;
    
    @Min(1)
    private int floor;
    
    @Min(1)
    private int capacity;
    
    private String roomType;
    private LocalDateTime createdAt;
    
    // Constructors, getters, setters
}
```

### Bed Entity
```java
@Document(collection = "beds")
public class Bed {
    @Id
    private String id;
    
    @NotBlank
    private String roomId;
    
    @Min(1)
    private int bedNumber;
    
    @NotBlank
    private String status; // "available" or "occupied"
    
    private String studentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors, getters, setters
}
```

### RoomChangeRequest Entity
```java
@Document(collection = "room_change_requests")
public class RoomChangeRequest {
    @Id
    private String id;
    
    @NotBlank
    private String studentId;
    
    private String currentRoomId;
    private int currentBedNumber;
    
    @NotBlank
    private String requestedRoomId;
    
    @Min(1)
    private int requestedBedNumber;
    
    @NotBlank
    private String reason;
    
    @NotBlank
    private String status; // "pending", "approved", "rejected"
    
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private String processedBy;
    private String comments;
    
    // Constructors, getters, setters
}
```

## 📋 API Endpoints

### Authentication Endpoints

#### POST /api/login
```java
@PostMapping("/api/login")
public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
    // Authentication logic
}
```

#### GET /api/profile
```java
@GetMapping("/api/profile")
@PreAuthorize("hasRole('USER')")
public ResponseEntity<User> getProfile(Authentication auth) {
    // Get user profile
}
```

#### POST /api/change-password
```java
@PostMapping("/api/change-password")
@PreAuthorize("hasRole('USER')")
public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication auth) {
    // Change password logic
}
```

### Student Management (Warden Only)

#### POST /api/warden/create-student
```java
@PostMapping("/api/warden/create-student")
@PreAuthorize("hasRole('WARDEN')")
public ResponseEntity<StudentResponse> createStudent(@Valid @RequestBody StudentRequest request) {
    // Create student logic
}
```

#### GET /api/warden/students
```java
@GetMapping("/api/warden/students")
@PreAuthorize("hasRole('WARDEN')")
public ResponseEntity<List<StudentDto>> getAllStudents() {
    // Get all students
}
```

#### POST /api/warden/assign-room
```java
@PostMapping("/api/warden/assign-room")
@PreAuthorize("hasRole('WARDEN')")
public ResponseEntity<String> assignRoom(@Valid @RequestBody RoomAssignmentRequest request) {
    // Room assignment logic
}
```

### Room Management

#### GET /api/rooms
```java
@GetMapping("/api/rooms")
@PreAuthorize("hasRole('USER')")
public ResponseEntity<List<RoomDto>> getAllRooms() {
    // Get all rooms with statistics
}
```

#### GET /api/rooms/{id}
```java
@GetMapping("/api/rooms/{id}")
@PreAuthorize("hasRole('USER')")
public ResponseEntity<RoomDetailsDto> getRoomDetails(@PathVariable String id) {
    // Get detailed room information
}
```

### Student Operations

#### GET /api/student/my-room
```java
@GetMapping("/api/student/my-room")
@PreAuthorize("hasRole('STUDENT')")
public ResponseEntity<MyRoomDto> getMyRoom(Authentication auth) {
    // Get current room assignment
}
```

#### POST /api/student/room-change-request
```java
@PostMapping("/api/student/room-change-request")
@PreAuthorize("hasRole('STUDENT')")
public ResponseEntity<String> submitRoomChangeRequest(@Valid @RequestBody RoomChangeRequestDto request, Authentication auth) {
    // Submit room change request
}
```

## 🔧 Configuration

### Application Configuration (`application.yml`)
```yaml
spring:
  application:
    name: hostel-management-system
  
  data:
    mongodb:
      uri: mongodb://localhost:27017/hostel_management
      database: hostel_management
  
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${app.jwt.issuer-uri:http://localhost:8080}

server:
  port: 8080
  servlet:
    context-path: /

app:
  jwt:
    secret: ${JWT_SECRET:hostel_management_secret_key_2024}
    expiration: 86400000 # 24 hours
    issuer-uri: http://localhost:8080

logging:
  level:
    com.hostel: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
```

### Security Configuration
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/login").permitAll()
                .requestMatchers("/api/warden/**").hasRole("WARDEN")
                .requestMatchers("/api/student/**").hasRole("STUDENT")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

### JWT Configuration
```java
@Component
public class JwtUtil {
    
    @Value("${app.jwt.secret}")
    private String secret;
    
    @Value("${app.jwt.expiration}")
    private int jwtExpiration;
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
    
    // Other JWT utility methods
}
```

## 🛡️ Security Features

### Authentication & Authorization
- **JWT Token-based authentication**
- **Role-based access control** (STUDENT, WARDEN)
- **Method-level security** with `@PreAuthorize`
- **Password encryption** with BCrypt
- **CORS configuration** for cross-origin requests

### Validation
- **Bean Validation (JSR-303)** annotations
- **Custom validators** for business rules
- **Input sanitization** and validation
- **Error handling** with global exception handler

### Data Protection
- **Password hashing** with BCrypt
- **JWT token expiration** management
- **Secure headers** configuration
- **SQL injection protection** through Spring Data

## 🔨 Development

### Available Maven Commands
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Package application
mvn package

# Run application
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Development Profiles
- **default**: Production configuration
- **dev**: Development configuration with debug logging
- **test**: Test configuration with in-memory database

### Testing
```bash
# Run unit tests
mvn test

# Run integration tests
mvn integration-test

# Generate test coverage report
mvn jacoco:report
```

## 📊 Database Setup

### MongoDB Installation

#### Windows
```bash
# Download and install MongoDB Community Server
# Start MongoDB service
net start MongoDB
```

#### macOS
```bash
# Install via Homebrew
brew tap mongodb/brew
brew install mongodb-community

# Start MongoDB
brew services start mongodb/brew/mongodb-community
```

#### Linux (Ubuntu)
```bash
# Import MongoDB public GPG key
wget -qO - https://www.mongodb.org/static/pgp/server-4.4.asc | sudo apt-key add -

# Create list file for MongoDB
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu focal/mongodb-org/4.4 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-4.4.list

# Install MongoDB
sudo apt-get update
sudo apt-get install -y mongodb-org

# Start MongoDB
sudo systemctl start mongod
```

### Database Initialization
The application automatically creates:
1. **Default warden account**
2. **Sample rooms and beds**
3. **Sample food menu**
4. **Required indexes**

## 🚨 Troubleshooting

### Common Issues

1. **MongoDB Connection Error**
   ```bash
   # Check MongoDB status
   sudo systemctl status mongod
   
   # Start MongoDB if not running
   sudo systemctl start mongod
   ```

2. **Port Conflicts**
   ```bash
   # Change port in application.yml
   server:
     port: 8081
   ```

3. **JWT Issues**
   ```bash
   # Set JWT secret environment variable
   export JWT_SECRET=your_secret_key_here
   ```

4. **Maven Build Issues**
   ```bash
   # Clean and reinstall dependencies
   mvn clean install -U
   ```

### Debugging
```bash
# Enable debug logging
java -jar target/hostel-management-0.0.1-SNAPSHOT.jar --logging.level.com.hostel=DEBUG

# Profile with specific configuration
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

## 📚 Service Layer Documentation

### AuthService
Handles authentication and user management:
- User authentication and JWT token generation
- Password validation and change operations
- User profile management

### StudentService
Manages student lifecycle:
- Student creation with validation
- Room assignment and management
- Room change request processing

### RoomService
Handles room and bed management:
- Room occupancy tracking
- Bed assignment operations
- Statistics calculation

### FoodMenuService
Manages food menu operations:
- Weekly menu management
- Menu item CRUD operations

## 📈 Performance Considerations

### Database Optimization
- **Indexing**: Critical fields are indexed
- **Connection Pooling**: MongoDB connection pooling configured
- **Query Optimization**: Efficient query design

### Caching
- **Spring Cache**: Method-level caching for frequent operations
- **Redis Integration**: Ready for Redis cache implementation

### Monitoring
- **Actuator Endpoints**: Health checks and metrics
- **Logging**: Comprehensive logging with different levels
- **Metrics**: Application metrics and monitoring

## 📝 API Documentation

### Swagger Integration
Access API documentation at: `http://localhost:8080/swagger-ui.html`

### Response Format
```json
{
  "success": true,
  "data": { /* response data */ },
  "message": "Operation completed successfully",
  "timestamp": "2024-01-01T00:00:00Z"
}
```

---

🚀 **Spring Boot Backend Ready!** The Spring Boot application provides an enterprise-grade, scalable API with comprehensive security, validation, and MongoDB integration following best practices and layered architecture patterns. 