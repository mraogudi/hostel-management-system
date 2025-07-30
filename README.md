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

## 📱 Features

### For Wardens
- 🏠 **Room Management**: View all rooms and their occupancy status
- 👥 **Student Management**: Create new student accounts with auto-generated passwords
- 🛏️ **Room Assignment**: Assign students to specific rooms and beds
- 📋 **Change Requests**: Review and manage room change requests from students
- 📊 **Dashboard**: Overview of hostel statistics and occupancy

### For Students
- 🏠 **My Room**: View current room details and roommate information
- 🍽️ **Food Menu**: Check weekly meal schedules
- 🔄 **Room Change**: Submit room change requests with reasons
- 👤 **Profile**: View personal information

## 🛠️ Technology Stack

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.2
- **Security**: Spring Security with JWT
- **Database**: MongoDB with Spring Data
- **Validation**: Bean Validation (JSR-303)
- **Password Encryption**: BCrypt

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
- `GET /api/rooms` - Get all rooms with statistics
- `GET /api/food-menu` - Get weekly food menu
- `POST /api/warden/create-student` - Create new student account
- `POST /api/warden/assign-room` - Assign room to student
- `POST /api/student/room-change-request` - Submit room change request

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