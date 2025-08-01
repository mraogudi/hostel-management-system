# Hostel Management System - MySQL Backend

This is the MySQL version of the Hostel Management System backend, built with Spring Boot and MySQL database.

## Features

- User authentication and authorization with JWT
- Student and Warden role-based access control
- Room and bed management
- Room change request system
- Food menu management
- MySQL database with JPA/Hibernate

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+

## Database Setup

1. Install MySQL Server and create a database:

```sql
CREATE DATABASE hostel_management;
CREATE USER 'hostel_user'@'localhost' IDENTIFIED BY 'hostel_password';
GRANT ALL PRIVILEGES ON hostel_management.* TO 'hostel_user'@'localhost';
FLUSH PRIVILEGES;
```

2. Update the database configuration in `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hostel_management?createDatabaseIfNotExist=true
    username: hostel_user  # Change to your MySQL username
    password: hostel_password  # Change to your MySQL password
```

## Running the Application

1. Clone the repository and navigate to the MySQL server directory:
```bash
cd server-spring-boot-mysql
```

2. Build and run the application:
```bash
mvn clean install
mvn spring-boot:run
```

The server will start on port 8081 (different from the MongoDB version on 8080).

## Default Credentials

The application initializes with default users:

### Warden Account:
- Username: `warden`
- Password: `warden123`

### Sample Student Account:
- Username: `STU001`
- Password: `student123`

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `GET /api/auth/profile` - Get user profile
- `POST /api/auth/change-password` - Change password

### Warden Endpoints
- `GET /api/warden/students` - Get all students
- `POST /api/warden/students` - Create new student
- `GET /api/warden/students/{id}` - Get student by ID
- `PUT /api/warden/students/{id}` - Update student
- `DELETE /api/warden/students/{id}` - Delete student
- `POST /api/warden/assign-room` - Assign room to student
- `GET /api/warden/rooms` - Get all rooms
- `GET /api/warden/room-change-requests` - Get all room change requests
- `PUT /api/warden/room-change-requests/{id}/approve` - Approve room change request
- `PUT /api/warden/room-change-requests/{id}/reject` - Reject room change request

### Student Endpoints
- `POST /api/student/room-change-request` - Submit room change request
- `GET /api/student/my-room` - Get current room details

### Common Endpoints
- `GET /api/rooms/{id}` - Get room details
- `GET /api/food-menu` - Get food menu

## Database Schema

The application uses JPA entities with MySQL-specific configurations:

- **users**: User information (students and wardens)
- **rooms**: Room details and capacity
- **beds**: Individual bed assignments
- **room_change_requests**: Room change requests from students
- **food_menu**: Weekly meal schedules

## Key Differences from MongoDB Version

1. **ID Types**: Uses `Long` auto-generated IDs instead of MongoDB ObjectIds
2. **Database**: MySQL with JPA/Hibernate instead of MongoDB
3. **Port**: Runs on port 8081 instead of 8080
4. **Relationships**: Proper foreign key relationships between entities
5. **Validation**: Column constraints and database-level validations

## Development

To modify the database schema, update the entity classes and set `spring.jpa.hibernate.ddl-auto=update` in the configuration. For production, use `validate` or `none` and manage schema changes with database migration tools.

## Troubleshooting

1. **Database Connection Issues**: Verify MySQL is running and credentials are correct
2. **Port Conflicts**: Change the server port in `application.yml` if 8081 is in use
3. **Schema Issues**: Drop and recreate the database if you encounter schema conflicts during development 