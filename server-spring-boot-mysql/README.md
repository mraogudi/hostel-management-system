# Hostel Management System - Backend (Spring Boot + MySQL)

A production-ready Spring Boot backend API for the hostel management system with JWT authentication, MySQL database integration, and enterprise-grade features.

## 🚀 Quick Start

### Prerequisites
- **Java** (v17 or higher)
- **Maven** (v3.6 or higher)
- **MySQL** (v8.0 or higher)

### Installation & Setup

```bash
# Navigate to Spring Boot MySQL directory
cd server-spring-boot-mysql

# Start MySQL service
sudo systemctl start mysql

# Create database
mysql -u root -p
CREATE DATABASE hostel_management;
EXIT;

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
- **Database**: MySQL 8.0 with Spring Data JPA
- **ORM**: Hibernate/JPA
- **Connection Pooling**: HikariCP
- **Validation**: Bean Validation (JSR-303)
- **Password Encryption**: BCrypt
- **Migration**: Flyway database migrations

### Project Structure
```
server-spring-boot-mysql/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── hostel/
│   │   │           ├── HostelManagementApplication.java
│   │   │           ├── controller/
│   │   │           ├── service/
│   │   │           ├── repository/
│   │   │           ├── entity/
│   │   │           ├── dto/
│   │   │           ├── config/
│   │   │           └── exception/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       └── db/
│   │           └── migration/
│   │               ├── V1__Initial_schema.sql
│   │               ├── V2__Add_personal_details_requests.sql
│   │               └── V3__Add_indexes.sql
├── pom.xml
└── README.md
```

## 📊 Database Schema

### User Entity (JPA)
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    @Column(name = "full_name", nullable = false)
    private String fullName;
    
    @Email
    private String email;
    
    @Pattern(regexp = "^[6-9]\\d{9}$")
    private String phone;
    
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @Column(name = "roll_number", unique = true)
    private String rollNumber;
    
    private String stream;
    private String branch;
    
    // Address fields
    @Column(name = "address_line1")
    private String addressLine1;
    
    @Column(name = "address_line2")
    private String addressLine2;
    
    private String city;
    private String state;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    // Guardian fields
    @Column(name = "guardian_name")
    private String guardianName;
    
    @Column(name = "guardian_phone")
    private String guardianPhone;
    
    @Column(name = "guardian_address")
    private String guardianAddress;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors, getters, setters
}
```

### Room Entity (JPA)
```java
@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "room_number", unique = true, nullable = false)
    private String roomNumber;
    
    @Column(nullable = false)
    private Integer floor;
    
    @Column(nullable = false)
    private Integer capacity;
    
    @Column(name = "room_type")
    private String roomType;
    
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Bed> beds = new ArrayList<>();
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // Constructors, getters, setters
}
```

### Bed Entity (JPA)
```java
@Entity
@Table(name = "beds")
public class Bed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
    
    @Column(name = "bed_number", nullable = false)
    private Integer bedNumber;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BedStatus status;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors, getters, setters
}
```

### Room Change Request Entity (JPA)
```java
@Entity
@Table(name = "room_change_requests")
public class RoomChangeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_room_id")
    private Room currentRoom;
    
    @Column(name = "current_bed_number")
    private Integer currentBedNumber;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_room_id", nullable = false)
    private Room requestedRoom;
    
    @Column(name = "requested_bed_number", nullable = false)
    private Integer requestedBedNumber;
    
    @Column(nullable = false, length = 1000)
    private String reason;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status;
    
    @CreationTimestamp
    @Column(name = "requested_at")
    private LocalDateTime requestedAt;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    private User processedBy;
    
    private String comments;
    
    // Constructors, getters, setters
}
```

## 🔧 Configuration

### Database Configuration (`application.yml`)
```yaml
spring:
  application:
    name: hostel-management-system-mysql
  
  datasource:
    url: jdbc:mysql://localhost:3306/hostel_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver
    
    # HikariCP connection pool settings
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      max-lifetime: 1200000
      connection-timeout: 20000
      leak-detection-threshold: 60000
  
  jpa:
    hibernate:
      ddl-auto: validate  # Use Flyway for schema management
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
        use_sql_comments: true
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
    
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    table: schema_version

server:
  port: 8080

app:
  jwt:
    secret: ${JWT_SECRET:hostel_management_mysql_secret_key_2024}
    expiration: 86400000 # 24 hours

logging:
  level:
    com.hostel: INFO
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Production Configuration (`application-prod.yml`)
```yaml
spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:hostel_management}?useSSL=true&requireSSL=true
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10
  
  jpa:
    show-sql: false
    properties:
      hibernate:
        generate_statistics: false
        
logging:
  level:
    com.hostel: WARN
    org.hibernate.SQL: WARN
    org.springframework.web: WARN
```

## 📋 Database Migrations (Flyway)

### V1__Initial_schema.sql
```sql
-- Users table
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('STUDENT', 'WARDEN') NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(15),
    date_of_birth DATE,
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    roll_number VARCHAR(50) UNIQUE,
    stream VARCHAR(100),
    branch VARCHAR(100),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(10),
    guardian_name VARCHAR(255),
    guardian_phone VARCHAR(15),
    guardian_address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Rooms table
CREATE TABLE rooms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_number VARCHAR(20) UNIQUE NOT NULL,
    floor INT NOT NULL,
    capacity INT NOT NULL,
    room_type VARCHAR(50) DEFAULT 'STANDARD',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Beds table
CREATE TABLE beds (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    room_id BIGINT NOT NULL,
    bed_number INT NOT NULL,
    status ENUM('AVAILABLE', 'OCCUPIED') NOT NULL DEFAULT 'AVAILABLE',
    student_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY unique_room_bed (room_id, bed_number)
);

-- Room change requests table
CREATE TABLE room_change_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    current_room_id BIGINT,
    current_bed_number INT,
    requested_room_id BIGINT NOT NULL,
    requested_bed_number INT NOT NULL,
    reason TEXT NOT NULL,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    processed_by BIGINT,
    comments TEXT,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (current_room_id) REFERENCES rooms(id) ON DELETE SET NULL,
    FOREIGN KEY (requested_room_id) REFERENCES rooms(id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Food menu table
CREATE TABLE food_menu (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    day_of_week ENUM('MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY') NOT NULL,
    meal_type ENUM('BREAKFAST', 'LUNCH', 'DINNER') NOT NULL,
    items TEXT NOT NULL,
    UNIQUE KEY unique_day_meal (day_of_week, meal_type)
);
```

### V2__Add_personal_details_requests.sql
```sql
-- Personal details update requests table
CREATE TABLE personal_details_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    phone VARCHAR(15),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(10),
    guardian_name VARCHAR(255),
    guardian_phone VARCHAR(15),
    guardian_address TEXT,
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP NULL,
    processed_by BIGINT,
    comments TEXT,
    FOREIGN KEY (student_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (processed_by) REFERENCES users(id) ON DELETE SET NULL
);
```

### V3__Add_indexes.sql
```sql
-- Performance optimization indexes
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_roll_number ON users(roll_number);
CREATE INDEX idx_beds_status ON beds(status);
CREATE INDEX idx_beds_room_id ON beds(room_id);
CREATE INDEX idx_room_change_requests_status ON room_change_requests(status);
CREATE INDEX idx_room_change_requests_student_id ON room_change_requests(student_id);
CREATE INDEX idx_personal_details_requests_status ON personal_details_requests(status);
CREATE INDEX idx_personal_details_requests_student_id ON personal_details_requests(student_id);

-- Composite indexes for common queries
CREATE INDEX idx_beds_room_status ON beds(room_id, status);
CREATE INDEX idx_requests_student_status ON room_change_requests(student_id, status);
```

## 📋 Repository Layer (Spring Data JPA)

### Custom Query Examples
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    @Query("SELECT u FROM User u WHERE u.role = :role")
    List<User> findByRole(@Param("role") Role role);
    
    @Query("SELECT u FROM User u WHERE u.rollNumber = :rollNumber")
    Optional<User> findByRollNumber(@Param("rollNumber") String rollNumber);
    
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.phone = :phone")
    boolean existsByPhone(@Param("phone") String phone);
}

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    
    @Query("SELECT r, COUNT(b.id) as occupiedBeds FROM Room r " +
           "LEFT JOIN r.beds b ON b.status = 'OCCUPIED' " +
           "GROUP BY r.id")
    List<Object[]> findRoomsWithOccupancy();
    
    @Query("SELECT r FROM Room r WHERE r.floor = :floor ORDER BY r.roomNumber")
    List<Room> findByFloorOrderByRoomNumber(@Param("floor") Integer floor);
}

@Repository
public interface BedRepository extends JpaRepository<Bed, Long> {
    
    @Query("SELECT b FROM Bed b WHERE b.room.id = :roomId AND b.status = 'AVAILABLE'")
    List<Bed> findAvailableBedsByRoomId(@Param("roomId") Long roomId);
    
    @Query("SELECT b FROM Bed b WHERE b.student.id = :studentId")
    Optional<Bed> findByStudentId(@Param("studentId") Long studentId);
    
    @Modifying
    @Query("UPDATE Bed b SET b.status = 'AVAILABLE', b.student = null WHERE b.student.id = :studentId")
    void releaseBedByStudentId(@Param("studentId") Long studentId);
}
```

## 🛡️ Advanced Security Configuration

### JWT Security with MySQL
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Autowired
    private JwtRequestFilter jwtRequestFilter;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Stronger encryption
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/login", "/api/health", "/actuator/**").permitAll()
                .requestMatchers("/api/warden/**").hasRole("WARDEN")
                .requestMatchers("/api/student/**").hasRole("STUDENT")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers
                .frameOptions().deny()
                .contentTypeOptions().and()
                .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .maxAgeInSeconds(31536000)
                    .includeSubdomains(true)
                )
            );
        
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
```

## 🔨 Development & Testing

### Maven Profiles
```xml
<profiles>
    <profile>
        <id>dev</id>
        <properties>
            <spring.profiles.active>dev</spring.profiles.active>
        </properties>
    </profile>
    <profile>
        <id>prod</id>
        <properties>
            <spring.profiles.active>prod</spring.profiles.active>
        </properties>
    </profile>
</profiles>
```

### Running with Profiles
```bash
# Development mode
mvn spring-boot:run -Pdev

# Production mode
mvn spring-boot:run -Pprod

# With specific database
mvn spring-boot:run -Dspring.datasource.url=jdbc:mysql://prod-server:3306/hostel_db
```

### Testing Configuration
```java
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false"
})
@ActiveProfiles("test")
public class RepositoryTest {
    // Test implementation
}
```

## 📊 Monitoring & Actuator

### Actuator Configuration
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,flyway
  endpoint:
    health:
      show-details: when-authorized
      show-components: always
  metrics:
    export:
      prometheus:
        enabled: true
```

### Custom Health Indicators
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {
    
    @Autowired
    private DataSource dataSource;
    
    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return Health.up()
                    .withDetail("database", "MySQL")
                    .withDetail("status", "Connected")
                    .build();
            }
        } catch (SQLException e) {
            return Health.down()
                .withDetail("database", "MySQL")
                .withDetail("error", e.getMessage())
                .build();
        }
        return Health.down().build();
    }
}
```

## 🚨 Production Considerations

### Database Optimization
```sql
-- Query optimization
EXPLAIN SELECT * FROM users WHERE roll_number = 'CS2024001';

-- Index analysis
SHOW INDEX FROM room_change_requests;

-- Performance monitoring
SHOW PROCESSLIST;
SHOW STATUS LIKE 'Slow_queries';
```

### Connection Pool Tuning
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 50
      minimum-idle: 10
      idle-timeout: 600000
      max-lifetime: 1800000
      connection-timeout: 30000
      leak-detection-threshold: 60000
      pool-name: HostelManagementCP
```

### Backup Strategy
```bash
# Database backup
mysqldump -u root -p --single-transaction --routines --triggers hostel_management > backup_$(date +%Y%m%d).sql

# Restore database
mysql -u root -p hostel_management < backup_20240101.sql
```

## 🔧 Deployment

### Docker Configuration
```dockerfile
FROM openjdk:17-jdk-slim

VOLUME /tmp

COPY target/hostel-management-mysql-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/app.jar"]
```

### Docker Compose
```yaml
version: '3.8'
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: hostel_management
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
  
  app:
    build: .
    depends_on:
      - mysql
    environment:
      DB_HOST: mysql
      DB_USERNAME: root
      DB_PASSWORD: rootpassword
    ports:
      - "8080:8080"

volumes:
  mysql_data:
```

---

🚀 **Production-Ready Backend!** The Spring Boot MySQL application provides enterprise-grade features with relational database integrity, advanced security, comprehensive monitoring, and production deployment capabilities. 