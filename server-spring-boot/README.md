# Hostel Management System - Spring Boot Backend

This is a Spring Boot backend implementation of the Hostel Management System using Java, MongoDB, and JWT authentication.

## Features

- **User Authentication & Authorization**: JWT-based authentication with role-based access control
- **User Management**: Create and manage student accounts (Warden functionality)
- **Room Management**: Room and bed allocation system
- **Food Menu Management**: Display weekly food menu
- **Room Change Requests**: Students can request room changes, wardens can view and manage requests
- **MongoDB Integration**: All data stored in MongoDB with proper relationships

## Tech Stack

- **Java 17**
- **Spring Boot 3.2.1**
- **Spring Security** with JWT
- **Spring Data MongoDB**
- **Maven** for dependency management
- **MongoDB** as database

## Prerequisites

1. **Java 17** or higher
2. **Maven 3.6+**
3. **MongoDB** running on localhost:27017

## Setup Instructions

### 1. Install MongoDB

Make sure MongoDB is installed and running on your system:

```bash
# For Windows (using Chocolatey)
choco install mongodb

# For macOS (using Homebrew)
brew tap mongodb/brew
brew install mongodb-community

# For Ubuntu/Debian
sudo apt update
sudo apt install mongodb

# Start MongoDB service
# Windows: MongoDB should start automatically
# macOS: brew services start mongodb-community
# Ubuntu/Debian: sudo systemctl start mongod
```

### 2. Clone and Setup

```bash
# Navigate to the Spring Boot server directory
cd server-spring-boot

# Install dependencies and build
mvn clean install
```

### 3. Configuration

The application uses the following default configuration (in `src/main/resources/application.yml`):

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/hostel_management

server:
  port: 8080

app:
  jwt:
    secret: hostel_management_secret_key_2024_spring_boot_version
    expiration: 86400000 # 24 hours
```

### 4. Run the Application

```bash
# Using Maven
mvn spring-boot:run

# Or run the JAR file after building
java -jar target/hostel-management-system-0.0.1-SNAPSHOT.jar
```

The server will start on `http://localhost:8080`

## Default Credentials

After the first run, the system creates a default warden account:

- **Username**: `warden`
- **Password**: `warden123`
- **Role**: `warden`

## API Endpoints

### Authentication
- `POST /api/login` - User login
- `GET /api/profile` - Get user profile (authenticated)

### Warden Operations
- `POST /api/warden/create-student` - Create a new student account
- `POST /api/warden/assign-room` - Assign room to student
- `GET /api/warden/room-change-requests` - View all room change requests

### Room Management
- `GET /api/rooms` - Get all rooms with statistics
- `GET /api/rooms/{roomId}` - Get room details with bed information

### Student Operations
- `POST /api/student/room-change-request` - Submit room change request
- `GET /api/student/my-room` - Get current room information

### Food Menu
- `GET /api/food-menu` - Get weekly food menu

## Database Schema

The application uses the following MongoDB collections:

### users
```json
{
  "_id": "ObjectId",
  "username": "string",
  "password": "string (hashed)",
  "role": "string (student/warden)",
  "fullName": "string",
  "email": "string",
  "phone": "string",
  "createdAt": "datetime"
}
```

### rooms
```json
{
  "_id": "ObjectId",
  "roomNumber": "string",
  "floor": "number",
  "capacity": "number",
  "occupiedBeds": "number",
  "roomType": "string",
  "createdAt": "datetime"
}
```

### beds
```json
{
  "_id": "ObjectId",
  "roomId": "string",
  "bedNumber": "number",
  "studentId": "string",
  "status": "string (available/occupied)"
}
```

### food_menu
```json
{
  "_id": "ObjectId",
  "mealType": "string (breakfast/lunch/dinner)",
  "dayOfWeek": "string",
  "items": "string",
  "createdAt": "datetime"
}
```

### room_change_requests
```json
{
  "_id": "ObjectId",
  "studentId": "string",
  "currentRoomId": "string",
  "requestedRoomId": "string",
  "reason": "string",
  "status": "string (pending/approved/rejected)",
  "requestedAt": "datetime",
  "processedAt": "datetime",
  "processedBy": "string"
}
```

## Authentication

The API uses JWT (JSON Web Token) for authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## CORS Configuration

The application is configured to allow CORS from all origins for development purposes. In production, update the CORS configuration in `SecurityConfig.java` to restrict origins.

## Development

### Project Structure
```
src/main/java/com/hostel/
├── config/                 # Configuration classes
├── controller/            # REST controllers
├── dto/                   # Data Transfer Objects
├── model/                 # Entity models
├── repository/            # MongoDB repositories
├── security/              # Security configuration
└── service/               # Business logic services
```

### Building for Production

```bash
# Create production JAR
mvn clean package -DskipTests

# The JAR file will be created in target/ directory
```

## Testing the API

You can use tools like Postman, curl, or any HTTP client to test the API endpoints.

### Example: Login Request
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "warden",
    "password": "warden123"
  }'
```

### Example: Get Rooms (with token)
```bash
curl -X GET http://localhost:8080/api/rooms \
  -H "Authorization: Bearer <your-jwt-token>"
```

## Troubleshooting

1. **MongoDB Connection Issues**: Ensure MongoDB is running and accessible at `mongodb://localhost:27017`
2. **Port Already in Use**: Change the port in `application.yml` if 8080 is occupied
3. **JWT Token Issues**: Ensure the token is properly included in the Authorization header
4. **Build Issues**: Make sure you have Java 17 or higher and Maven 3.6+

## Contributing

This Spring Boot backend provides the same functionality as the Node.js server but with enterprise-grade features and better scalability. 