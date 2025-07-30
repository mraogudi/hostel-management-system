# Hostel Management System

A complete hostel management system built with **React frontend** and **Spring Boot backend**, featuring JWT authentication, room management, and student operations.

## 🏗️ Architecture

- **Frontend**: React 18 with React Router and Axios
- **Backend**: Spring Boot 3 with MongoDB
- **Authentication**: JWT tokens
- **Database**: MongoDB

## 🚀 Quick Start

### Prerequisites

- **Node.js** (v16 or higher)
- **Java** (v17 or higher)
- **Maven** (v3.6 or higher)
- **MongoDB** (v4.4 or higher)

### 1. Start MongoDB

Make sure MongoDB is running on `mongodb://localhost:27017`

```bash
# Start MongoDB service
# On Windows:
net start MongoDB

# On macOS/Linux:
sudo systemctl start mongod
# or
brew services start mongodb/brew/mongodb-community
```

### 2. Start the Backend (Spring Boot)

```bash
# Navigate to Spring Boot directory
cd server-spring-boot

# Install dependencies and run
mvn clean install
mvn spring-boot:run

# The backend will start on http://localhost:8080
```

### 3. Start the Frontend (React)

```bash
# Open a new terminal and navigate to client directory
cd client

# Install dependencies
npm install

# Start the development server
npm start

# The frontend will start on http://localhost:3000
```

## 🔑 Default Credentials

After the first run, the system creates a default warden account:

- **Username**: `warden`
- **Password**: `warden123`
- **Role**: `warden`

**For Students**: Students receive auto-generated credentials when their account is created by the warden:
- **Username**: Their Roll Number (e.g., if roll number is "CS2024001", username is "CS2024001")
- **Password**: Auto-generated 8-character secure password
- **Role**: `student`

**🔐 First-Time Login Security**: Students must change their password on first login before accessing the dashboard.

## 📱 Features

### For Wardens
- 🏠 **Room Management**: View all rooms and their occupancy status
- 👥 **Enhanced Student Management**: Create comprehensive student profiles with detailed information
- 🛏️ **Room Assignment**: Assign students to specific rooms and beds
- 📋 **Change Requests**: Review and manage room change requests from students
- 📊 **Dashboard**: Overview of hostel statistics and occupancy

#### 🆕 Enhanced Student Registration
The student registration form now captures comprehensive student information:

**Personal Information:**
- Full Name
- Date of Birth with validation
- Gender (Male/Female/Other)
- 12-digit Aadhaar ID with automatic formatting

**Contact Information:**
- Email Address (optional)
- 10-digit Phone Number with automatic formatting and validation

**Phone Number Validation Features:**
- ✅ Required field with comprehensive validation
- ✅ Must be exactly 10 digits
- ✅ Must start with 6, 7, 8, or 9 (Indian mobile number format)
- ✅ Real-time validation feedback with error/success messages
- ✅ Automatic formatting (numbers only)
- ✅ Duplicate phone number checking
- ✅ Enhanced visual feedback with validation states

**Academic Information:**
- Roll Number (auto-formatted to uppercase, used as login username)
- Stream (Engineering, Medical, Commerce, Arts, Science, Management)
- Branch/Specialization

**Login System:**
- ✅ **Simplified Authentication**: Roll number serves as the username
- ✅ **Auto-generated passwords**: Secure 8-character passwords created automatically
- ✅ **No separate username field**: Eliminates confusion and simplifies the process

**Features:**
- ✅ Real-time validation and formatting
- ✅ Duplicate checking for Aadhaar ID, Roll Number, and Phone Number
- ✅ Auto-generated secure passwords
- ✅ **Simplified login**: Roll number used as username
- ✅ Comprehensive success message with student details
- ✅ Form reset functionality
- ✅ Mobile-responsive design with sectioned layout

### 🎯 **Key Features Implemented:**

✅ **Simplified Authentication System**: Roll number serves as username  
✅ **First-Time Login Security**: Mandatory password change for new students  
✅ **Service Layer Architecture**: Clean separation of business logic from controllers  
✅ **Real-time input formatting and validation**  
✅ **Duplicate checking for sensitive fields**  
✅ **Sectioned form layout for better UX**  
✅ **Mobile-responsive design**  
✅ **Enhanced success messages with complete student details**  
✅ **Automatic password generation**  
✅ **Form reset functionality**  
✅ **Enhanced student profile display**  
✅ **Comprehensive error handling**  
✅ **MongoDB integration with proper indexing**  
✅ **Protected routing with role-based access**

## 🏗️ **Architecture Overview**

The application follows a **layered architecture** pattern with clear separation of concerns:

### **Controller Layer** (`@RestController`)
- Handles HTTP requests and responses
- Input validation and error handling
- Maps endpoints to service methods
- Maintains clean, focused controllers

### **Service Layer** (`@Service`)
- **AuthService**: User authentication, password management, profile operations
- **StudentService**: Student lifecycle management, room assignments, change requests
- **RoomService**: Room management, occupancy tracking, statistics
- **FoodMenuService**: Food menu operations and data management

### **Repository Layer** (`@Repository`)
- Data access and database operations
- Spring Data MongoDB integration
- Custom query methods

### **Model Layer** (`@Document`)
- Entity definitions with MongoDB mapping
- Field validation annotations
- JSON serialization configuration

This architecture provides:
- ✅ **Maintainability**: Business logic centralized in services
- ✅ **Testability**: Services can be unit tested independently
- ✅ **Scalability**: Easy to modify business rules without affecting controllers
- ✅ **Code Reusability**: Services can be used across multiple controllers
- ✅ **Single Responsibility**: Each layer has a clear, focused purpose

### 📋 Field Validation Summary

| Field | Type | Validation Rules | Required |
|-------|------|------------------|----------|
| Full Name | Text | Not blank | ✅ |
| Date of Birth | Date | Must be in past | ✅ |
| Gender | Select | Male/Female/Other | ✅ |
| Aadhaar ID | Text | 12 digits, unique | ✅ |
| Roll Number | Text | Unique, auto-uppercase, used as username | ✅ |
| Stream | Select | Predefined options | ✅ |
| Branch | Text | Not blank | ✅ |
| Phone Number | Tel | 10 digits starting with 6-9, unique | ✅ |
| Email | Email | Valid format | ❌ |

**Note**: Roll Number serves as both the student identifier and login username, simplifying the authentication process.

### For Students
- 🏠 **Enhanced Profile & Room View**: Comprehensive student profile display with room details
- 🍽️ **Food Menu**: Check weekly meal schedules
- 🔄 **Room Change**: Submit room change requests with reasons
- 👤 **Detailed Profile**: View all personal and academic information
- 🔐 **Secure First Login**: Mandatory password change on first login

#### 🆕 Enhanced Student Profile Display
Students can now view their complete profile information including:
- Personal details (Name, DOB, Gender, Aadhaar ID)
- Contact information (Email, Phone)
- Academic details (Roll Number, Stream, Branch)
- Room assignment and roommate information

#### 🔐 First-Time Login Security Flow
**Enhanced Security Features:**
- ✅ **Mandatory Password Change**: Students must change password on first login
- ✅ **Secure Validation**: Current password verification required
- ✅ **Real-time Feedback**: Password strength and match validation
- ✅ **Auto-redirect**: Seamless flow to dashboard after password change
- ✅ **Protected Routes**: Cannot access dashboard until password is changed

## 🛠️ Technology Stack

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.2
- **Security**: Spring Security with JWT
- **Database**: MongoDB with Spring Data
- **Architecture**: Layered architecture with Service Layer separation
- **Validation**: Bean Validation (JSR-303)
- **Password Encryption**: BCrypt

**Service Layer Components:**
- **AuthService**: Authentication and password management
- **StudentService**: Student creation, room assignment, and requests
- **RoomService**: Room management and occupancy tracking
- **FoodMenuService**: Food menu operations and management

### Frontend (React)
- **Framework**: React 18
- **Routing**: React Router v6
- **HTTP Client**: Axios
- **State Management**: React Context API
- **Authentication**: JWT token management

## 📊 Database Collections

The application uses MongoDB with the following collections:

- **users**: Student and warden accounts
- **rooms**: Room information and capacity
- **beds**: Individual bed assignments
- **room_change_requests**: Student room change requests
- **food_menu**: Weekly meal schedules

## 🔗 API Integration

The React frontend automatically connects to the Spring Boot backend through:

- **Proxy Configuration**: All API calls are proxied to `http://localhost:8080`
- **JWT Authentication**: Automatic token management in HTTP headers
- **Error Handling**: Comprehensive error handling for API failures
- **CORS**: Properly configured for cross-origin requests

### Key API Endpoints

- `POST /api/login` - User authentication
- `GET /api/profile` - Get user profile
- `POST /api/change-password` - Change user password (authenticated)
- `GET /api/rooms` - Get all rooms with statistics
- `GET /api/food-menu` - Get weekly food menu
- `POST /api/warden/create-student` - Create new student account
- `POST /api/warden/assign-room` - Assign room to student (uses roll number as student identifier)
- `POST /api/student/room-change-request` - Submit room change request

### Student Management (Warden Only)
- `POST /api/warden/create-student` - Create new student
- `GET /api/warden/students` - Get all students with room assignments
- `GET /api/warden/students/{id}` - Get detailed student information
- `PUT /api/warden/students/{id}` - Update student information
- `DELETE /api/warden/students/{id}` - Delete student (handles room cleanup)
- `POST /api/warden/students/{id}/reset-password` - Reset student password
- `POST /api/warden/assign-room` - Assign room to student (uses roll number as student identifier)
- `GET /api/warden/room-change-requests` - Get all room change requests

## 📋 **API Endpoints**

### Authentication Endpoints
- `POST /api/login` - User authentication
- `GET /api/profile` - Get user profile
- `POST /api/change-password` - Change user password (first-time login requirement)

### Student Management (Warden Only)
- `POST /api/warden/create-student` - Create new student
- `GET /api/warden/students` - Get all students with room assignments
- `GET /api/warden/students/{id}` - Get detailed student information
- `PUT /api/warden/students/{id}` - Update student information
- `DELETE /api/warden/students/{id}` - Delete student (handles room cleanup)
- `POST /api/warden/students/{id}/reset-password` - Reset student password
- `POST /api/warden/assign-room` - Assign room to student (uses roll number as student identifier)
- `GET /api/warden/room-change-requests` - Get all room change requests

### Student Operations
- `GET /api/student/my-room` - Get current room assignment and roommates
- `POST /api/student/room-change-request` - Submit room change request

### General Endpoints
- `GET /api/rooms` - Get all rooms with occupancy statistics
- `GET /api/rooms/{id}` - Get detailed room information with bed assignments
- `GET /api/food-menu` - Get weekly food menu

#### **Room Assignment API Format:**
```json
POST /api/warden/assign-room
{
  "studentId": "CS2021001",    // Student's roll number (not MongoDB ObjectId)
  "roomId": "room_object_id",  // Room's MongoDB ObjectId  
  "bedNumber": 1               // Bed number within the room
}
```

## 🔧 Configuration

### Backend Configuration (`application.yml`)

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/hostel_management
server:
  port: 8080
app:
  jwt:
    secret: your-secret-key
    expiration: 86400000 # 24 hours
```

### Frontend Configuration

The React app is configured to proxy API requests to the Spring Boot backend:

```json
{
  "proxy": "http://localhost:8080"
}
```

## 🚨 Troubleshooting

### Common Issues

1. **MongoDB Connection Error**
   - Ensure MongoDB is running on port 27017
   - Check MongoDB service status

2. **CORS Issues**
   - All Spring Boot controllers are configured with `@CrossOrigin(origins = "*")`
   - Ensure both frontend and backend are running

3. **Port Conflicts**
   - Backend runs on port 8080
   - Frontend runs on port 3000
   - Make sure these ports are available

4. **Authentication Issues**
   - JWT tokens are stored in localStorage
   - Check browser dev tools for token presence
   - Default warden credentials are created on first startup

### Development Tips

- **Hot Reload**: Frontend supports hot reload for development
- **Database Reset**: Drop the `hostel_management` database to reset all data
- **Logs**: Check Spring Boot console for detailed error logs
- **Network Tab**: Use browser dev tools to debug API calls

## 📝 License

This project is built for educational purposes and hostel management automation.

---

🚀 **Ready to go!** Start both servers and navigate to `http://localhost:3000` to access the application. 

#### **Student Dashboard Features:**
- **🏠 Room Information**: Complete room and roommate details
- **🍽️ Food Menu**: Weekly meal schedules organized by day and meal type
- **🔄 Room Change Requests**: Enhanced request system with:
  - **📋 Current Room Display**: Shows current room details, bed assignment, and roommates
  - **🛏️ Bed Selection**: Choose specific bed when requesting room change
  - **✅ Real-time Validation**: Ensures requested beds are available
  - **🎯 Smart Filtering**: Excludes current room from available options
  - **💬 Detailed Reasons**: Text area for change justification

#### **Warden Dashboard Features:**

#### **Student Management:**
- **📋 Students List**: View all students with key information (name, roll no, phone, DOB, room assignment)
- **👁️ View Details**: Comprehensive student profile popup with all personal, contact, academic, and room information
- **✏️ Edit Student**: Update student information with real-time validation
- **🔑 Password Reset**: Generate new passwords for students (triggers first-time login flow)
- **🗑️ Delete Student**: Remove students with confirmation (automatically handles room deallocation and cleanup)
- **📊 Room Assignment Status**: See which students have rooms assigned and which don't

#### **Room Change Management:**
- **📋 Request Overview**: View all room change requests with detailed information
- **🛏️ Bed-Specific Requests**: See exact bed numbers requested by students
- **👤 Student Context**: Full student information and current room details
- **⏰ Request Tracking**: Timestamps and processing status 