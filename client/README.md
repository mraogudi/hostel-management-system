# Hostel Management System - Frontend (React)

A modern, responsive React frontend for the hostel management system with role-based authentication and comprehensive dashboard features.

## 🚀 Quick Start

### Prerequisites
- **Node.js** (v16 or higher)
- **npm** (v8 or higher)

### Installation & Setup

```bash
# Navigate to client directory
cd client

# Install dependencies
npm install

# Start the development server
npm start

# The frontend will start on http://localhost:3000
```

## 🏗️ Architecture

### Technology Stack
- **Framework**: React 18 with Hooks
- **Routing**: React Router v6 with protected routes
- **State Management**: React Context API for global state
- **HTTP Client**: Fetch API with custom apiCall wrapper
- **Styling**: Custom CSS with responsive design
- **Authentication**: JWT token management with auto-refresh

### Project Structure
```
client/
├── public/
│   ├── index.html
│   └── manifest.json
├── src/
│   ├── components/
│   │   ├── StudentDashboard.js        # Student main dashboard
│   │   ├── StudentDashboard.css       # Student dashboard styles
│   │   ├── WardenDashboard.js         # Warden main dashboard
│   │   ├── WardenDashboard.css        # Warden dashboard styles
│   │   ├── Login.js                   # Authentication component
│   │   ├── Login.css                  # Login page styles
│   │   ├── ChangePassword.js          # Password change component
│   │   └── ChangePassword.css         # Password change styles
│   ├── context/
│   │   └── AuthContext.js             # Authentication context
│   ├── App.js                         # Main app component
│   ├── App.css                        # Global styles
│   └── index.js                       # Entry point
├── package.json
└── README.md
```

## 🎨 Features & Components

### Authentication System
- **JWT Token Management**: Automatic token storage and refresh
- **Protected Routes**: Role-based access control (student/warden)
- **Password Change**: Mandatory first-time password change for students
- **Session Persistence**: Login state maintained across browser sessions

### Student Dashboard Features

#### 🏠 Profile & Room Information
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

### Warden Dashboard Features

#### 📊 Enhanced Statistics Dashboard
- **Primary Stats**: Total rooms, available beds, occupied beds, total students
- **Secondary Stats Grid**: 6 comprehensive stat cards:
  - ✅ Assigned Students (students with rooms)
  - ❌ Unassigned Students (students needing room assignment)
  - 📋 Total Requests (all room change + personal details requests)
  - 🆔 Personal Details Requests (profile update requests)
  - 🏠 Room Change Requests (room transfer requests)
  - 🎯 Occupancy Rate (bed utilization percentage)

#### 👥 Student Management
- **Create Students**: Comprehensive registration with validation
- **View Students**: Complete student listing with room assignments
- **Edit Students**: Update student information
- **Room Assignment**: Assign students to specific rooms and beds
- **Password Reset**: Generate new passwords for students

#### 🏠 Room Management
- **Room Overview**: Visual room cards with bed availability indicators
- **Floor Organization**: Rooms organized by floor levels
- **Bed Layouts**: Detailed bed assignments with student information
- **Real-time Status**: Live occupancy and availability updates

#### 📋 Request Management
- **Room Change Requests**: Review and approve/reject room transfers
- **Personal Details Requests**: Review and approve/reject profile updates
- **Request History**: Track all requests with timestamps
- **Comments System**: Add comments when processing requests

## 🎨 UI/UX Features

### Responsive Design
- **Mobile-First**: Responsive design that works on all devices
- **Modern UI**: Clean, intuitive interface with hover effects
- **Loading States**: Visual feedback during API calls
- **Error Handling**: Comprehensive error messages and validation

### Interactive Components
- **Visual Room Selection**: Interactive room cards with availability indicators
- **Stat Cards**: Animated statistics cards with hover effects
- **Modal Systems**: Clean modal interfaces for detailed information
- **Form Validation**: Real-time form validation with visual feedback

### CSS Architecture
- **Component-Specific Styles**: Each component has its own CSS file
- **Responsive Breakpoints**: Mobile (768px), tablet (1024px), desktop
- **Modern CSS Features**: Grid, flexbox, animations, gradients
- **Custom Properties**: Consistent color scheme and spacing

## 🔧 Configuration

### Environment Setup
The React app includes proxy configuration for API calls:

```json
{
  "proxy": "http://localhost:5000"
}
```

### API Integration
- **Base URL**: Automatically proxied to backend server
- **Authentication**: JWT tokens automatically included in headers
- **Error Handling**: Comprehensive error handling for all API calls
- **Loading States**: UI feedback during API operations

## 📱 API Endpoints Used

### Authentication
- `POST /api/login` - User authentication
- `GET /api/profile` - Get user profile
- `POST /api/change-password` - Change password

### Student Operations
- `GET /api/student/my-room` - Get current room assignment
- `POST /api/student/room-change-request` - Submit room change request
- `POST /api/student/personal-details-update-request` - Submit personal details update
- `GET /api/student/warden-contact` - Get warden contact information

### Warden Operations
- `POST /api/warden/create-student` - Create new student
- `GET /api/warden/students` - Get all students
- `POST /api/warden/assign-room` - Assign room to student
- `GET /api/warden/room-change-requests` - Get room change requests
- `PUT /api/warden/room-change-requests/:id/:action` - Process room requests
- `GET /api/warden/personal-details-update-requests` - Get personal details requests
- `PUT /api/warden/personal-details-update-requests/:id/:action` - Process personal requests

### General
- `GET /api/rooms` - Get all rooms with statistics
- `GET /api/rooms/:roomId` - Get detailed room information
- `GET /api/food-menu` - Get weekly food menu

## 🔨 Development

### Available Scripts
```bash
# Start development server
npm start

# Build for production
npm run build

# Run tests
npm test

# Eject from Create React App (irreversible)
npm run eject
```

### Development Tips
- **Hot Reload**: Automatic refresh on file changes
- **React DevTools**: Use browser extension for debugging
- **Network Tab**: Monitor API calls in browser dev tools
- **Console Debugging**: Check browser console for errors

### Code Organization
- **Component Separation**: Each dashboard is a separate component
- **Context Usage**: Global state managed through AuthContext
- **API Abstraction**: Centralized API calls through apiCall function
- **Error Boundaries**: Proper error handling throughout the app

## 📚 Component Documentation

### AuthContext
Provides global authentication state and functions:
- `user`: Current logged-in user information
- `login(username, password)`: Authenticate user
- `logout()`: Clear session and redirect to login
- `apiCall(method, url, data)`: Authenticated API calls
- `fetchProfile()`: Refresh user profile

### StudentDashboard
Main dashboard for student users with tabs:
- **Room Tab**: Profile and room information
- **Menu Tab**: Weekly food menu
- **Request Tab**: Room change request form
- **Personal Details Tab**: Update profile information

### WardenDashboard
Comprehensive dashboard for warden users with tabs:
- **Overview Tab**: Statistics and quick actions
- **Students Tab**: Student management interface
- **Rooms Tab**: Room and bed management
- **Requests Tab**: Process student requests

### Login Component
Authentication interface with:
- Username/password form
- Role-based redirects
- Error handling
- Responsive design

### ChangePassword Component
Password change interface for:
- First-time login (mandatory)
- Voluntary password changes
- Current password verification
- Password strength validation

## 🚨 Troubleshooting

### Common Issues

1. **Build Errors**
   - Clear node_modules: `rm -rf node_modules && npm install`
   - Clear npm cache: `npm cache clean --force`

2. **API Connection Issues**
   - Ensure backend server is running on port 5000
   - Check browser network tab for failed requests
   - Verify proxy configuration in package.json

3. **Authentication Issues**
   - Check localStorage for JWT tokens
   - Verify token expiration
   - Clear localStorage and re-login

4. **Styling Issues**
   - Check for CSS specificity conflicts
   - Verify responsive breakpoints
   - Use browser dev tools to debug styles

### Performance Optimization
- **Code Splitting**: Components are loaded as needed
- **Memoization**: Expensive calculations are memoized
- **Image Optimization**: Icons and images are optimized
- **Bundle Analysis**: Use `npm run build` to analyze bundle size

## 📝 Contributing

### Code Style
- Use functional components with hooks
- Follow React best practices
- Maintain component separation
- Use meaningful variable names
- Add comments for complex logic

### Testing
- Test authentication flows
- Verify responsive design
- Check API error handling
- Validate form submissions

---

🎨 **Frontend Ready!** The React application provides a modern, responsive interface for both students and wardens with comprehensive dashboard features and real-time updates. 