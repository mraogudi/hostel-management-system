# Hostel Management System

A comprehensive web application for managing hostel operations, designed for both students and wardens.

## Features

### For Students:
- **Room Details**: View assigned room information and roommates
- **Food Menu**: Check daily meal schedules and menus
- **Room Change Requests**: Submit requests to change rooms with reasons

### For Wardens (Admin):
- **Student Management**: Create student accounts with auto-generated credentials
- **Room Assignment**: Assign students to specific rooms and beds
- **Room Management**: View detailed room occupancy and bed assignments
- **Request Management**: Review and process room change requests
- **Dashboard Overview**: Get insights into hostel occupancy statistics

## Technology Stack

- **Frontend**: React.js with modern UI components
- **Backend**: Node.js with Express.js
- **Database**: SQLite (for development)
- **Authentication**: JWT (JSON Web Tokens)
- **Styling**: CSS3 with modern design patterns

## Project Structure

```
hostel-management-system/
├── server/
│   ├── index.js          # Main server file with all APIs
│   └── database.db       # SQLite database (auto-generated)
├── client/
│   ├── public/
│   │   └── index.html
│   ├── src/
│   │   ├── components/
│   │   │   ├── Login.js           # Login component
│   │   │   ├── StudentDashboard.js # Student portal
│   │   │   ├── WardenDashboard.js  # Warden portal
│   │   │   └── *.css             # Component styles
│   │   ├── context/
│   │   │   └── AuthContext.js    # Authentication context
│   │   ├── App.js               # Main app component
│   │   ├── App.css             # Global styles
│   │   └── index.js            # App entry point
│   └── package.json            # Client dependencies
├── package.json                # Server dependencies
└── README.md                  # This file
```

## Installation & Setup

### Prerequisites
- Node.js (v14 or higher)
- npm (v6 or higher)

### Installation Steps

1. **Clone or navigate to the project directory:**
   ```bash
   cd Workspace/hostel-management-system
   ```

2. **Install server dependencies:**
   ```bash
   npm install
   ```

3. **Install client dependencies:**
   ```bash
   cd client
   npm install
   cd ..
   ```

4. **Run the application:**
   ```bash
   npm run dev
   ```

   This will start both the backend server (port 5000) and React frontend (port 3000).

### Alternative: Run separately

**Backend only:**
```bash
npm run server
```

**Frontend only:**
```bash
npm run client
```

## Usage

### Default Login Credentials

**Warden (Admin):**
- Username: `warden`
- Password: `warden123`

**Students:**
Student accounts must be created by the warden. The warden can create student accounts and will receive auto-generated login credentials to share with students.

### Getting Started

1. **Access the application**: Open http://localhost:3000 in your browser
2. **Login as Warden**: Use the default warden credentials
3. **Create Student Accounts**: Navigate to "Create Student" tab
4. **Assign Rooms**: Use "Assign Rooms" tab to allocate beds to students
5. **Manage Requests**: Monitor room change requests in "Change Requests" tab

## Database Schema

The application automatically creates the following tables:

- **users**: Stores student and warden account information
- **rooms**: Contains room details (10 rooms, 3 beds each)
- **beds**: Individual bed assignments
- **food_menu**: Weekly meal schedules
- **room_change_requests**: Student room change requests

## API Endpoints

### Authentication
- `POST /api/login` - User authentication
- `GET /api/profile` - Get user profile

### Student Endpoints
- `GET /api/student/my-room` - Get assigned room details
- `POST /api/student/room-change-request` - Submit room change request

### Warden Endpoints
- `POST /api/warden/create-student` - Create student account
- `POST /api/warden/assign-room` - Assign room to student
- `GET /api/warden/room-change-requests` - Get all room change requests

### General Endpoints
- `GET /api/rooms` - Get all rooms with occupancy
- `GET /api/rooms/:id` - Get specific room details
- `GET /api/food-menu` - Get food menu

## Features in Detail

### Room Management
- **10 Rooms Total**: Each room has 3 beds (30 total capacity)
- **Floor Assignment**: Rooms are distributed across multiple floors
- **Real-time Occupancy**: Track available vs occupied beds
- **Bed-level Assignment**: Assign specific beds to students

### Food Menu System
- **Weekly Schedule**: Different menus for each day
- **Meal Types**: Breakfast, lunch, and dinner
- **Student Access**: Students can view current week's menu

### Request System
- **Room Changes**: Students can request room changes with reasons
- **Approval Workflow**: Wardens can approve or reject requests
- **Status Tracking**: Monitor request status and history

## Development

### Adding New Features
1. **Backend**: Add new routes in `server/index.js`
2. **Frontend**: Create new components in `client/src/components/`
3. **Database**: Modify schema in the database initialization section

### Styling
- Component-specific styles in individual CSS files
- Global styles in `App.css`
- Responsive design with mobile-first approach

## Security Features

- **JWT Authentication**: Secure token-based authentication
- **Role-based Access**: Different permissions for students and wardens
- **Password Hashing**: Secure password storage using bcrypt
- **Input Validation**: Server-side validation for all inputs

## Troubleshooting

### Common Issues

1. **Port already in use**: Change ports in package.json scripts
2. **Database not found**: Server will auto-create database on first run
3. **React app won't start**: Ensure client dependencies are installed

### Development Mode
- Backend logs all API calls to console
- Frontend includes error boundaries for debugging
- Database recreates tables if needed

## Future Enhancements

- **Payment Integration**: Hostel fee management
- **Notifications**: Real-time notifications for requests
- **Advanced Reporting**: Detailed analytics and reports
- **Mobile App**: Native mobile application
- **Guest Management**: Visitor registration system

## Support

For technical support or feature requests, please refer to the application documentation or contact the development team.

---

**Note**: This is a development version. For production deployment, additional security measures and environment configurations should be implemented. 