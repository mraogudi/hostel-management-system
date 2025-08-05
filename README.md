# Hostel Management System

A comprehensive hostel management system built with **React frontend** and **Node.js/Express backend**, featuring JWT authentication, room management, student operations, and request management.

## 🏗️ Architecture

- **Frontend**: React 18 with React Router and Context API
- **Backend**: Node.js with Express.js
- **Authentication**: JWT tokens with bcrypt encryption
- **Database**: JSON file-based storage with real-time persistence
- **Styling**: Custom CSS with responsive design

## 🚀 Quick Start

### Prerequisites

- **Node.js** (v16 or higher)
- **npm** (v8 or higher)

### 1. Start the Backend (Node.js/Express)

```bash
# Navigate to server directory
cd server

# Install dependencies
npm install

# Start the server
npm start

# The backend will start on http://localhost:5000
```

### 2. Start the Frontend (React)

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
- **Username**: Their Roll Number (e.g., "CS2024001")
- **Password**: Auto-generated 8-character secure password
- **Role**: `student`

**🔐 First-Time Login Security**: Students must change their password on first login before accessing the dashboard.

## 📱 Features

### For Wardens

#### 📊 Enhanced Dashboard with Statistics
- **Primary Stats**: Total rooms, available beds, occupied beds, total students
- **Secondary Stats Grid**: 
  - ✅ Assigned Students (students with rooms)
  - ❌ Unassigned Students (students needing room assignment)
  - 📋 Total Requests (all room change + personal details requests)
  - 🆔 Personal Details Requests (profile update requests)
  - 🏠 Room Change Requests (room transfer requests)
  - 🎯 Occupancy Rate (bed utilization percentage)

#### 🏠 Room Management
- View all rooms with real-time occupancy status
- Visual room cards with bed availability indicators
- Floor-wise room organization
- Detailed bed layouts with student assignments

#### 👥 Enhanced Student Management
- **Create Students**: Comprehensive registration with validation
- **View Students**: Complete student listing with room assignments
- **Edit Students**: Update student information
- **Room Assignment**: Assign students to specific rooms and beds
- **Password Reset**: Generate new passwords for students

#### 📋 Request Management
- **Room Change Requests**: Review and approve/reject room transfers
- **Personal Details Requests**: Review and approve/reject profile updates
- **Request History**: Track all requests with timestamps
- **Comments System**: Add comments when processing requests

#### 🔍 Enhanced Student Registration
**Personal Information:**
- Full Name with validation
- Date of Birth
- Gender (Male/Female/Other)
- Email Address (optional)
- Phone Number with Indian format validation

**Academic Information:**
- Roll Number (auto-formatted, used as username)
- Stream (Engineering, Medical, Commerce, Arts, Science, Management)
- Branch/Specialization

**Address Information:**
- Address lines, city, state, postal code
- Guardian details (name, phone, address)

### For Students

#### 🏠 Enhanced Profile & Dashboard
- **Student Information Card**: Complete profile with personal details
- **Current Room Card**: Room details, floor, capacity, and roommates
- **Modern UI**: Clean, responsive design with hover effects

#### 🔄 Room Change Requests
- **Visual Room Selection**: Interactive room cards with availability status
- **Bed Selection**: Choose specific beds within rooms
- **Request Tracking**: View request status and history
- **Reason Required**: Detailed explanation for room changes

#### 🆔 Personal Details Update
- **Profile Management**: Update personal information
- **Guardian Information**: Update guardian contact details
- **Address Management**: Update residential addresses
- **Request-Based Updates**: All changes require warden approval

#### 🍽️ Food Menu
- **Weekly Schedule**: View meals organized by day
- **Meal Types**: Breakfast, lunch, and dinner menus
- **Responsive Layout**: Mobile-friendly menu display

#### 📞 Warden Contact
- **Contact Information**: Easy access to warden details
- **Emergency Contact**: Quick access to emergency numbers
- **Office Hours**: Warden availability information

## 🛠️ Technology Stack

### Backend (Node.js/Express)
- **Framework**: Express.js
- **Authentication**: JWT with bcrypt password hashing
- **Database**: JSON file-based storage with real-time persistence
- **Middleware**: CORS, body-parser, dotenv
- **Architecture**: RESTful API with modular endpoint organization

### Frontend (React)
- **Framework**: React 18 with Hooks
- **Routing**: React Router v6 with protected routes
- **State Management**: React Context API for global state
- **HTTP Client**: Fetch API with custom apiCall wrapper
- **Styling**: Custom CSS with responsive design
- **Authentication**: JWT token management with auto-refresh

## 📊 Database Structure

The application uses JSON file-based storage (`database.json`) with the following collections:

### Users Collection
```json
{
  "id": 1,
  "username": "CS2024001",
  "password": "hashed_password",
  "role": "student|warden",
  "full_name": "Student Name",
  "email": "student@example.com",
  "phone": "9876543210",
  "date_of_birth": "2000-01-01",
  "gender": "Male",
  "roll_number": "CS2024001",
  "stream": "Engineering",
  "branch": "Computer Science",
  "address_line1": "Address",
  "city": "City",
  "state": "State",
  "postal_code": "123456",
  "guardian_name": "Guardian Name",
  "guardian_phone": "9876543210"
}
```

### Rooms Collection
```json
{
  "id": 1,
  "room_number": "R001",
  "floor": 1,
  "capacity": 3,
  "room_type": "standard"
}
```

### Beds Collection
```json
{
  "id": 1,
  "room_id": 1,
  "bed_number": 1,
  "status": "available|occupied",
  "student_id": null
}
```

### Room Change Requests
```json
{
  "id": 1,
  "student_id": 2,
  "current_room_id": 1,
  "current_bed_number": 1,
  "requested_room_id": 2,
  "requested_bed_number": 2,
  "reason": "Request reason",
  "status": "pending|approved|rejected",
  "requested_at": "2024-01-01T00:00:00.000Z",
  "processed_at": null,
  "processed_by": null
}
```

### Personal Details Requests
```json
{
  "id": 1,
  "student_id": 2,
  "phone": "9876543210",
  "address_line1": "New Address",
  "city": "New City",
  "guardian_name": "New Guardian",
  "status": "pending|approved|rejected",
  "requested_at": "2024-01-01T00:00:00.000Z",
  "processed_at": null,
  "processed_by": null,
  "comments": null
}
```

### Food Menu Collection
```json
{
  "id": 1,
  "day_of_week": "Monday",
  "meal_type": "breakfast",
  "items": "Breakfast items"
}
```

## 📋 API Endpoints

### Authentication Endpoints
- `POST /api/login` - User authentication
- `GET /api/profile` - Get user profile (authenticated)
- `POST /api/change-password` - Change user password (authenticated)

### Student Management (Warden Only)
- `POST /api/warden/create-student` - Create new student account
- `GET /api/warden/students` - Get all students with room assignments
- `POST /api/warden/assign-room` - Assign room to student

### Room Management
- `GET /api/rooms` - Get all rooms with occupancy statistics
- `GET /api/rooms/:roomId` - Get detailed room information with bed assignments

### Student Operations
- `GET /api/student/my-room` - Get current room assignment and roommates
- `POST /api/student/room-change-request` - Submit room change request
- `POST /api/student/personal-details-update-request` - Submit personal details update
- `GET /api/student/warden-contact` - Get warden contact information

### Request Management (Warden Only)
- `GET /api/warden/room-change-requests` - Get all room change requests
- `PUT /api/warden/room-change-requests/:id/:action` - Approve/reject room change requests
- `GET /api/warden/personal-details-update-requests` - Get all personal details requests
- `PUT /api/warden/personal-details-update-requests/:id/:action` - Approve/reject personal details requests

### General Endpoints
- `GET /api/food-menu` - Get weekly food menu

## 🎨 Frontend Features

### Responsive Design
- **Mobile-First**: Responsive design that works on all devices
- **Modern UI**: Clean, intuitive interface with hover effects
- **Loading States**: Visual feedback during API calls
- **Error Handling**: Comprehensive error messages and validation

### Authentication System
- **Protected Routes**: Role-based access control
- **Token Management**: Automatic token refresh and logout
- **Password Change**: Mandatory first-time password change for students
- **Session Persistence**: Login state maintained across browser sessions

### Interactive Components
- **Visual Room Selection**: Interactive room cards with availability indicators
- **Stat Cards**: Animated statistics cards with hover effects
- **Modal Systems**: Clean modal interfaces for detailed information
- **Form Validation**: Real-time form validation with visual feedback

## 🔧 Configuration

### Backend Configuration
The server uses environment variables and defaults:

```javascript
const PORT = process.env.PORT || 5000;
const JWT_SECRET = process.env.JWT_SECRET || 'hostel_management_secret_key_2024';
```

### Frontend Configuration
The React app includes proxy configuration for API calls:

```json
{
  "proxy": "http://localhost:5000"
}
```

## 🚨 Troubleshooting

### Common Issues

1. **Port Conflicts**
   - Backend runs on port 5000
   - Frontend runs on port 3000
   - Make sure these ports are available

2. **Database Issues**
   - Database file is created automatically on first run
   - Check `server/database.json` for data persistence
   - Delete database file to reset all data

3. **Authentication Issues**
   - JWT tokens are stored in localStorage
   - Check browser dev tools for token presence
   - Default warden credentials are created on first startup

4. **CORS Issues**
   - CORS is configured for all origins in development
   - Ensure both frontend and backend are running

### Development Tips

- **Hot Reload**: Frontend supports hot reload for development
- **Database Reset**: Delete `server/database.json` to reset all data
- **Logs**: Check server console for detailed error logs
- **Network Tab**: Use browser dev tools to debug API calls
- **State Debugging**: Use React Developer Tools for state inspection

## 📈 Recent Updates

### v2.0 - Enhanced Request Management
- ✅ Added Personal Details Update Requests system
- ✅ Enhanced Room Change Requests with bed selection
- ✅ Implemented Secondary Stats Grid with 6 stat cards
- ✅ Added comprehensive request approval/rejection workflow
- ✅ Improved warden dashboard with detailed statistics

### v1.5 - UI/UX Improvements
- ✅ Modern stat cards with hover animations
- ✅ Responsive design improvements
- ✅ Enhanced visual feedback and loading states
- ✅ Improved form validation and error handling

### v1.0 - Core Features
- ✅ Basic authentication and authorization
- ✅ Room and bed management
- ✅ Student registration and management
- ✅ Food menu system
- ✅ Basic room change requests

## 📝 License

This project is built for educational purposes and hostel management automation.

---

🚀 **Ready to go!** Start both servers and navigate to `http://localhost:3000` to access the application.

### Quick Setup Commands

```bash
# Terminal 1 - Start Backend
cd server && npm install && npm start

# Terminal 2 - Start Frontend  
cd client && npm install && npm start
```

The application will be available at `http://localhost:3000` with the backend API running on `http://localhost:5000`. 