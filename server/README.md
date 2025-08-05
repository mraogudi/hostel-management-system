# Hostel Management System - Backend (Node.js/Express)

A robust Node.js/Express backend API for the hostel management system with JWT authentication, comprehensive request management, and JSON file-based persistence.

## 🚀 Quick Start

### Prerequisites
- **Node.js** (v16 or higher)
- **npm** (v8 or higher)

### Installation & Setup

```bash
# Navigate to server directory
cd server

# Install dependencies
npm install

# Start the server
npm start

# The backend will start on http://localhost:5000
```

## 🏗️ Architecture

### Technology Stack
- **Framework**: Express.js
- **Authentication**: JWT with bcrypt password hashing
- **Database**: JSON file-based storage with real-time persistence
- **Middleware**: CORS, body-parser, dotenv
- **Architecture**: RESTful API with modular endpoint organization

### Project Structure
```
server/
├── index.js                    # Main server file with all endpoints
├── database.json              # JSON database file (auto-generated)
├── package.json               # Dependencies and scripts
├── .env                       # Environment variables (optional)
└── README.md                  # This file
```

## 📊 Database Structure

The application uses JSON file-based storage (`database.json`) with automatic persistence:

### Collections Overview
- **users**: Student and warden accounts with authentication data
- **rooms**: Room information and capacity details
- **beds**: Individual bed assignments and status
- **room_change_requests**: Student room transfer requests
- **personal_details_requests**: Student profile update requests
- **food_menu**: Weekly meal schedules

### Users Collection
```json
{
  "id": 1,
  "username": "CS2024001",
  "password": "hashed_password_with_bcrypt",
  "role": "student|warden",
  "full_name": "Student Name",
  "email": "student@example.com",
  "phone": "9876543210",
  "date_of_birth": "2000-01-01",
  "gender": "Male",
  "roll_number": "CS2024001",
  "stream": "Engineering",
  "branch": "Computer Science",
  "address_line1": "Address Line 1",
  "address_line2": "Address Line 2",
  "city": "City",
  "state": "State",
  "postal_code": "123456",
  "guardian_name": "Guardian Name",
  "guardian_phone": "9876543210",
  "guardian_address": "Guardian Address",
  "created_at": "2024-01-01T00:00:00.000Z",
  "updated_at": "2024-01-01T00:00:00.000Z"
}
```

### Rooms Collection
```json
{
  "id": 1,
  "room_number": "R001",
  "floor": 1,
  "capacity": 3,
  "room_type": "standard",
  "created_at": "2024-01-01T00:00:00.000Z"
}
```

### Beds Collection
```json
{
  "id": 1,
  "room_id": 1,
  "bed_number": 1,
  "status": "available|occupied",
  "student_id": null,
  "created_at": "2024-01-01T00:00:00.000Z",
  "updated_at": "2024-01-01T00:00:00.000Z"
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
  "address_line2": "New Address Line 2",
  "city": "New City",
  "state": "New State",
  "postal_code": "654321",
  "guardian_name": "New Guardian",
  "guardian_phone": "9876543210",
  "guardian_address": "New Guardian Address",
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
  "meal_type": "breakfast|lunch|dinner",
  "items": "Meal items description"
}
```

## 📋 API Endpoints

### Authentication Endpoints

#### POST /api/login
Authenticate user and return JWT token
```json
Request:
{
  "username": "CS2024001",
  "password": "userpassword"
}

Response:
{
  "success": true,
  "token": "jwt_token_here",
  "user": {
    "id": 1,
    "username": "CS2024001",
    "role": "student",
    "full_name": "Student Name"
  }
}
```

#### GET /api/profile
Get current user profile (requires authentication)
```json
Response:
{
  "success": true,
  "data": {
    "id": 1,
    "username": "CS2024001",
    "full_name": "Student Name",
    "email": "student@example.com",
    // ... other user fields
  }
}
```

#### POST /api/change-password
Change user password (requires authentication)
```json
Request:
{
  "currentPassword": "old_password",
  "newPassword": "new_password"
}

Response:
{
  "message": "Password changed successfully"
}
```

### Student Management Endpoints (Warden Only)

#### POST /api/warden/create-student
Create new student account
```json
Request:
{
  "full_name": "Student Name",
  "date_of_birth": "2000-01-01",
  "gender": "Male",
  "email": "student@example.com",
  "phone": "9876543210",
  "roll_number": "CS2024001",
  "stream": "Engineering",
  "branch": "Computer Science"
}

Response:
{
  "message": "Student created successfully",
  "student": { /* student object */ },
  "credentials": {
    "username": "CS2024001",
    "password": "generated_password"
  }
}
```

#### GET /api/warden/students
Get all students with room assignments
```json
Response:
{
  "success": true,
  "data": [
    {
      "id": 1,
      "full_name": "Student Name",
      "roll_number": "CS2024001",
      "room_number": "R001",
      "room_id": 1,
      "bed_number": 1,
      "floor": 1
      // ... other fields
    }
  ]
}
```

#### POST /api/warden/assign-room
Assign room to student
```json
Request:
{
  "studentId": 1,
  "roomId": 1,
  "bedNumber": 1
}

Response:
{
  "message": "Room assigned successfully"
}
```

### Room Management Endpoints

#### GET /api/rooms
Get all rooms with occupancy statistics
```json
Response:
{
  "success": true,
  "data": [
    {
      "id": 1,
      "room_number": "R001",
      "floor": 1,
      "capacity": 3,
      "occupied_beds": 2,
      "available_beds": 1
    }
  ]
}
```

#### GET /api/rooms/:roomId
Get detailed room information with bed assignments
```json
Response:
{
  "success": true,
  "data": {
    "id": 1,
    "room_number": "R001",
    "floor": 1,
    "capacity": 3,
    "beds": [
      {
        "id": 1,
        "bed_number": 1,
        "status": "occupied",
        "student_name": "Student Name"
      }
    ]
  }
}
```

### Student Operations

#### GET /api/student/my-room
Get current room assignment and roommates
```json
Response:
{
  "success": true,
  "data": {
    "room": {
      "id": 1,
      "room_number": "R001",
      "floor": 1,
      "capacity": 3
    },
    "bed": {
      "id": 1,
      "bed_number": 1
    },
    "roommates": [
      {
        "full_name": "Roommate Name",
        "roll_number": "CS2024002"
      }
    ]
  }
}
```

#### POST /api/student/room-change-request
Submit room change request
```json
Request:
{
  "requestedRoomId": 2,
  "requestedBedNumber": 1,
  "reason": "Reason for room change"
}

Response:
{
  "message": "Room change request submitted successfully"
}
```

#### POST /api/student/personal-details-update-request
Submit personal details update request
```json
Request:
{
  "phone": "9876543210",
  "address_line1": "New Address",
  "city": "New City",
  "guardian_name": "New Guardian Name"
  // ... other fields to update
}

Response:
{
  "message": "Personal details update request submitted successfully"
}
```

#### GET /api/student/warden-contact
Get warden contact information
```json
Response:
{
  "success": true,
  "data": {
    "name": "Hostel Warden",
    "email": "warden@hostel.edu",
    "phone": "9876543210",
    "emergency_contact": "9876543210",
    "office_hours": "Monday - Friday: 9:00 AM - 5:00 PM"
  }
}
```

### Request Management Endpoints (Warden Only)

#### GET /api/warden/room-change-requests
Get all room change requests
```json
Response:
{
  "success": true,
  "data": [
    {
      "id": 1,
      "student_name": "Student Name",
      "current_room": "R001",
      "requested_room": "R002",
      "requested_bed_number": 1,
      "reason": "Request reason",
      "status": "pending",
      "requested_at": "2024-01-01T00:00:00.000Z"
    }
  ]
}
```

#### PUT /api/warden/room-change-requests/:id/:action
Approve or reject room change request
```json
Request URL: /api/warden/room-change-requests/1/approve
Request Body:
{
  "comments": "Optional comment"
}

Response:
{
  "message": "Room change request approved successfully",
  "request": { /* updated request object */ }
}
```

#### GET /api/warden/personal-details-update-requests
Get all personal details update requests
```json
Response:
{
  "success": true,
  "data": [
    {
      "id": 1,
      "student_name": "Student Name",
      "student_roll_number": "CS2024001",
      "phone": "9876543210",
      "status": "pending",
      "requested_at": "2024-01-01T00:00:00.000Z"
      // ... other requested fields
    }
  ]
}
```

#### PUT /api/warden/personal-details-update-requests/:id/:action
Approve or reject personal details update request
```json
Request URL: /api/warden/personal-details-update-requests/1/approve
Request Body:
{
  "comments": "Optional comment"
}

Response:
{
  "message": "Personal details request approved successfully",
  "request": { /* updated request object */ }
}
```

### General Endpoints

#### GET /api/food-menu
Get weekly food menu
```json
Response:
{
  "success": true,
  "data": [
    {
      "id": 1,
      "day_of_week": "Monday",
      "meal_type": "breakfast",
      "items": "Breakfast items"
    }
  ]
}
```

## 🔧 Configuration

### Environment Variables
Create a `.env` file in the server directory:
```env
PORT=5000
JWT_SECRET=your_jwt_secret_key_here
```

### Default Configuration
If no environment variables are provided:
- **Port**: 5000
- **JWT Secret**: 'hostel_management_secret_key_2024'

### Database Initialization
On first startup, the server automatically creates:
1. **Database file** (`database.json`)
2. **Default warden account**:
   - Username: `warden`
   - Password: `warden123`
3. **Sample rooms** (R001-R010) with 3 beds each
4. **Sample food menu** for the week

## 🛡️ Authentication & Security

### JWT Implementation
- **Token Generation**: On successful login
- **Token Validation**: Middleware validates all protected routes
- **Token Expiration**: 24 hours (configurable)
- **Password Hashing**: bcrypt with salt rounds

### Protected Routes
All endpoints except `/api/login` require authentication:
```javascript
// Middleware function
const authenticateToken = (req, res, next) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  
  if (!token) {
    return res.status(401).json({ error: 'Access token required' });
  }
  
  jwt.verify(token, JWT_SECRET, (err, user) => {
    if (err) {
      return res.status(403).json({ error: 'Invalid token' });
    }
    req.user = user;
    next();
  });
};
```

### Role-Based Access Control
- **Student routes**: `/api/student/*` - Only students can access
- **Warden routes**: `/api/warden/*` - Only wardens can access
- **General routes**: `/api/rooms`, `/api/food-menu` - Both roles can access

## 🔨 Development

### Available Scripts
```bash
# Start the server
npm start

# Start with nodemon (for development)
npm run dev

# Install dependencies
npm install
```

### Database Management
```bash
# Reset database (delete database.json file)
rm database.json

# Backup database
cp database.json database_backup_$(date +%Y%m%d).json

# View database contents
cat database.json | jq .
```

### Helper Functions
The server includes utility functions:

#### getNextId(collection)
Generates unique IDs for database collections
```javascript
const getNextId = (collection) => {
  const items = db[collection];
  return items.length > 0 ? Math.max(...items.map(item => item.id)) + 1 : 1;
};
```

#### saveDatabase()
Persists database changes to file
```javascript
const saveDatabase = () => {
  try {
    fs.writeFileSync(dbPath, JSON.stringify(db, null, 2));
    console.log('Database saved successfully');
  } catch (error) {
    console.error('Error saving database:', error);
  }
};
```

## 🚨 Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Kill process on port 5000
   npx kill-port 5000
   
   # Or use different port
   PORT=5001 npm start
   ```

2. **Database File Corruption**
   ```bash
   # Backup and reset
   mv database.json database_corrupted.json
   npm start  # Will create new database
   ```

3. **JWT Token Issues**
   - Check JWT_SECRET environment variable
   - Verify token format in Authorization header
   - Ensure token hasn't expired

4. **Permission Errors**
   ```bash
   # Fix file permissions
   chmod 644 database.json
   ```

### Debugging
```bash
# Enable debug logging
DEBUG=* npm start

# Check server logs
tail -f server.log

# Monitor database changes
watch -n 1 'ls -la database.json'
```

### Performance Monitoring
- Monitor memory usage for large datasets
- Consider pagination for large collections
- Implement database indexing for production use

## 📚 API Response Format

### Success Response
```json
{
  "success": true,
  "data": { /* response data */ },
  "message": "Operation completed successfully"
}
```

### Error Response
```json
{
  "success": false,
  "error": "Error message",
  "details": "Additional error details"
}
```

### Status Codes
- **200**: Success
- **201**: Created
- **400**: Bad Request
- **401**: Unauthorized
- **403**: Forbidden
- **404**: Not Found
- **500**: Internal Server Error

## 📈 Future Enhancements

### Potential Improvements
1. **Database Migration**: Move to MongoDB/PostgreSQL for production
2. **Caching**: Implement Redis for better performance
3. **File Uploads**: Add support for profile pictures and documents
4. **Email Notifications**: Send emails for request approvals/rejections
5. **Audit Logging**: Track all database changes
6. **API Versioning**: Implement versioned API endpoints
7. **Rate Limiting**: Add rate limiting for API calls
8. **WebSocket**: Real-time updates for dashboard statistics

### Scalability Considerations
- Database connection pooling
- Horizontal scaling with load balancers
- Microservices architecture
- Docker containerization

---

🚀 **Backend Ready!** The Node.js/Express server provides a robust, scalable API with comprehensive authentication, request management, and real-time data persistence. 