const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const jwt = require('jsonwebtoken');
const bcrypt = require('bcryptjs');
const fs = require('fs');
const path = require('path');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 5000;
const JWT_SECRET = process.env.JWT_SECRET || 'hostel_management_secret_key_2024';

// Middleware
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Database file path
const dbPath = path.join(__dirname, 'database.json');

// Initialize database
let db = {
  users: [],
  rooms: [],
  beds: [],
  food_menu: [],
  room_change_requests: [],
  personal_details_requests: []
};

// Load database from file or create initial data
function loadDatabase() {
  try {
    if (fs.existsSync(dbPath)) {
      console.log('Loading database from file...');
      const data = fs.readFileSync(dbPath, 'utf8');
      const loadedDb = JSON.parse(data);
      
      // Validate database structure
      const requiredCollections = ['users', 'rooms', 'beds', 'food_menu', 'room_change_requests', 'personal_details_requests'];
      const missingCollections = requiredCollections.filter(collection => !loadedDb[collection]);
      
      if (missingCollections.length > 0) {
        console.warn(`Missing collections in database: ${missingCollections.join(', ')}`);
        // Initialize missing collections as empty arrays
        missingCollections.forEach(collection => {
          loadedDb[collection] = [];
        });
      }
      
      db = loadedDb;
      console.log(`✅ Database loaded successfully!`);
      console.log(`📊 Database statistics:`);
      console.log(`   - Users: ${db.users.length} (${db.users.filter(u => u.role === 'warden').length} wardens, ${db.users.filter(u => u.role === 'student').length} students)`);
      console.log(`   - Rooms: ${db.rooms.length}`);
      console.log(`   - Beds: ${db.beds.length} (${db.beds.filter(b => b.status === 'occupied').length} occupied, ${db.beds.filter(b => b.status === 'available').length} available)`);
      console.log(`   - Food Menu Items: ${db.food_menu.length}`);
      console.log(`   - Room Change Requests: ${db.room_change_requests.length}`);
      console.log(`   - Personal Details Requests: ${db.personal_details_requests.length}`);
      
      // Find and display default login credentials
      const warden = db.users.find(u => u.role === 'warden');
      if (warden) {
        console.log(`\n🔐 Default Login Credentials:`);
        console.log(`   Warden - Username: ${warden.username}, Password: warden123`);
        const students = db.users.filter(u => u.role === 'student').slice(0, 3);
        students.forEach(student => {
          console.log(`   Student - Username: ${student.username}, Password: password123`);
        });
        if (students.length < db.users.filter(u => u.role === 'student').length) {
          console.log(`   ... and ${db.users.filter(u => u.role === 'student').length - students.length} more students`);
        }
      }
      
    } else {
      console.log('Database file not found. Creating new database...');
      initializeDatabase();
    }
  } catch (error) {
    console.error('❌ Error loading database:', error.message);
    console.log('Creating new database with default data...');
    initializeDatabase();
  }
}

// Initialize database loading
loadDatabase();

function initializeDatabase() {
  console.log('Initializing new database...');
  
  // Create default warden account
  const defaultWardenPassword = bcrypt.hashSync('warden123', 10);
  db.users.push({
    id: 1,
    username: 'warden',
    password: defaultWardenPassword,
    role: 'warden',
    full_name: 'Hostel Warden',
    email: 'warden@hostel.edu',
    phone: '9876543210',
    created_at: new Date().toISOString()
  });

  // Create rooms and beds
  let roomId = 1;
  let bedId = 1;
  
  for (let i = 1; i <= 10; i++) {
    const roomNumber = `R${i.toString().padStart(3, '0')}`;
    const floor = Math.ceil(i / 4);
    
    db.rooms.push({
      id: roomId,
      room_number: roomNumber,
      floor: floor,
      capacity: 3,
      occupied_beds: 0,
      room_type: 'standard',
      created_at: new Date().toISOString()
    });

    // Create 3 beds for each room
    for (let bedNum = 1; bedNum <= 3; bedNum++) {
      db.beds.push({
        id: bedId++,
        room_id: roomId,
        bed_number: bedNum,
        student_id: null,
        status: 'available'
      });
    }
    roomId++;
  }

  // Create sample food menu
  const menuItems = [
    { meal_type: 'breakfast', day_of_week: 'Monday', items: 'Bread, Butter, Jam, Tea/Coffee, Boiled Eggs' },
    { meal_type: 'lunch', day_of_week: 'Monday', items: 'Rice, Dal, Vegetable Curry, Chapati, Pickle' },
    { meal_type: 'dinner', day_of_week: 'Monday', items: 'Rice, Sambar, Dry Vegetable, Chapati, Curd' },
    { meal_type: 'breakfast', day_of_week: 'Tuesday', items: 'Poha, Tea/Coffee, Banana' },
    { meal_type: 'lunch', day_of_week: 'Tuesday', items: 'Rice, Rasam, Vegetable Curry, Chapati, Papad' },
    { meal_type: 'dinner', day_of_week: 'Tuesday', items: 'Rice, Dal, Mixed Vegetable, Chapati, Pickle' },
    { meal_type: 'breakfast', day_of_week: 'Wednesday', items: 'Idli, Sambar, Chutney, Tea/Coffee' },
    { meal_type: 'lunch', day_of_week: 'Wednesday', items: 'Rice, Curd, Vegetable, Chapati, Pickle' },
    { meal_type: 'dinner', day_of_week: 'Wednesday', items: 'Rice, Dal, Fry, Chapati, Salad' }
  ];

  db.food_menu = menuItems.map((item, index) => ({
    id: index + 1,
    ...item,
    created_at: new Date().toISOString()
  }));

  saveDatabase();
}

function saveDatabase() {
  try {
    fs.writeFileSync(dbPath, JSON.stringify(db, null, 2));
  } catch (error) {
    console.error('Error saving database:', error);
  }
}

function getNextId(table) {
  if (db[table].length === 0) return 1;
  return Math.max(...db[table].map(item => item.id)) + 1;
}

// Authentication middleware
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

// Routes

// Login endpoint
app.post('/api/login', (req, res) => {
  try {
    const { username, password } = req.body;
    const user = db.users.find(u => u.username === username);

    if (!user || !bcrypt.compareSync(password, user.password)) {
      return res.status(401).json({ error: 'Invalid credentials' });
    }

    const token = jwt.sign(
      { id: user.id, username: user.username, role: user.role },
      JWT_SECRET,
      { expiresIn: '24h' }
    );

    res.json({
      token,
      user: {
        id: user.id,
        username: user.username,
        role: user.role,
        full_name: user.full_name,
        email: user.email
      }
    });
  } catch (error) {
    console.error('Login error:', error);
    res.status(500).json({ error: 'Database error' });
  }
});

// Get user profile
app.get('/api/profile', authenticateToken, (req, res) => {
  try {
    const user = db.users.find(u => u.id === req.user.id);
    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }
    
    const { password, ...userProfile } = user;
    res.json(userProfile);
  } catch (error) {
    console.error('Profile error:', error);
    res.status(500).json({ error: 'Database error' });
  }
});

// Warden endpoints
app.post('/api/warden/create-student', authenticateToken, (req, res) => {
  if (req.user.role !== 'warden') {
    return res.status(403).json({ error: 'Access denied' });
  }

  try {
    const { username, full_name, email, phone } = req.body;
    
    // Check if username already exists
    if (db.users.find(u => u.username === username)) {
      return res.status(400).json({ error: 'Username already exists' });
    }

    const password = Math.random().toString(36).slice(-8);
    const hashedPassword = bcrypt.hashSync(password, 10);
    const studentId = getNextId('users');

    const newStudent = {
      id: studentId,
      username,
      password: hashedPassword,
      role: 'student',
      full_name,
      email: email || null,
      phone: phone || null,
      created_at: new Date().toISOString()
    };

    db.users.push(newStudent);
    saveDatabase();

    res.json({
      message: 'Student created successfully',
      credentials: { username, password },
      student_id: studentId
    });
  } catch (error) {
    console.error('Create student error:', error);
    res.status(500).json({ error: 'Failed to create student' });
  }
});

// Get all rooms with bed information
app.get('/api/rooms', authenticateToken, (req, res) => {
  try {
    const roomsWithStats = db.rooms.map(room => {
      const roomBeds = db.beds.filter(bed => bed.room_id === room.id);
      const occupied_beds = roomBeds.filter(bed => bed.status === 'occupied').length;
      const available_beds = roomBeds.filter(bed => bed.status === 'available').length;
      
      return {
        ...room,
        occupied_beds,
        available_beds
      };
    });
    
    res.json(roomsWithStats);
  } catch (error) {
    console.error('Get rooms error:', error);
    res.status(500).json({ error: 'Database error' });
  }
});

// Get room details with beds
app.get('/api/rooms/:roomId', authenticateToken, (req, res) => {
  try {
    const roomId = parseInt(req.params.roomId);
    const room = db.rooms.find(r => r.id === roomId);

    if (!room) {
      return res.status(404).json({ error: 'Room not found' });
    }

    const beds = db.beds.filter(bed => bed.room_id === roomId).map(bed => {
      const student = bed.student_id ? db.users.find(u => u.id === bed.student_id) : null;
      return {
        ...bed,
        student_name: student ? student.full_name : null
      };
    });

    res.json({ ...room, beds });
  } catch (error) {
    console.error('Get room details error:', error);
    res.status(500).json({ error: 'Database error' });
  }
});

// Assign student to room
app.post('/api/warden/assign-room', authenticateToken, (req, res) => {
  if (req.user.role !== 'warden') {
    return res.status(403).json({ error: 'Access denied' });
  }

  try {
    const { student_id, room_id, bed_number } = req.body;

    const bed = db.beds.find(b => 
      b.room_id === parseInt(room_id) && 
      b.bed_number === parseInt(bed_number) && 
      b.status === 'available'
    );

    if (!bed) {
      return res.status(400).json({ error: 'Bed not available' });
    }

    // Update bed assignment
    bed.student_id = parseInt(student_id);
    bed.status = 'occupied';
    
    saveDatabase();
    res.json({ message: 'Room assigned successfully' });
  } catch (error) {
    console.error('Assign room error:', error);
    res.status(500).json({ error: 'Failed to assign room' });
  }
});

// Get food menu
app.get('/api/food-menu', authenticateToken, (req, res) => {
  try {
    const dayOrder = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday'];
    const mealOrder = ['breakfast', 'lunch', 'dinner'];
    
    const sortedMenu = db.food_menu.sort((a, b) => {
      const dayDiff = dayOrder.indexOf(a.day_of_week) - dayOrder.indexOf(b.day_of_week);
      if (dayDiff !== 0) return dayDiff;
      return mealOrder.indexOf(a.meal_type) - mealOrder.indexOf(b.meal_type);
    });
    
    res.json(sortedMenu);
  } catch (error) {
    console.error('Get food menu error:', error);
    res.status(500).json({ error: 'Database error' });
  }
});

// Submit room change request
app.post('/api/student/room-change-request', authenticateToken, (req, res) => {
  if (req.user.role !== 'student') {
    return res.status(403).json({ error: 'Access denied' });
  }

  try {
    const { requestedRoomId, requestedBedNumber, reason } = req.body;

    if (!requestedRoomId || !requestedBedNumber) {
      return res.status(400).json({ error: 'Room ID and bed number are required' });
    }

    // Get current room of student
    const currentBed = db.beds.find(bed => bed.student_id === req.user.id);
    const requestId = getNextId('room_change_requests');

    const newRequest = {
      id: requestId,
      student_id: req.user.id,
      current_room_id: currentBed ? currentBed.room_id : null,
      current_bed_number: currentBed ? currentBed.bed_number : null,
      requested_room_id: parseInt(requestedRoomId),
      requested_bed_number: parseInt(requestedBedNumber),
      reason,
      status: 'pending',
      requested_at: new Date().toISOString(),
      processed_at: null,
      processed_by: null
    };

    db.room_change_requests.push(newRequest);
    saveDatabase();

    res.json({ message: 'Room change request submitted successfully' });
  } catch (error) {
    console.error('Submit room change request error:', error);
    res.status(500).json({ error: 'Failed to submit request' });
  }
});

// Get room change requests (for warden)
app.get('/api/warden/room-change-requests', authenticateToken, (req, res) => {
  if (req.user.role !== 'warden') {
    return res.status(403).json({ error: 'Access denied' });
  }

  try {
    const requestsWithDetails = db.room_change_requests.map(request => {
      const student = db.users.find(u => u.id === request.student_id);
      const currentRoom = request.current_room_id ? db.rooms.find(r => r.id === request.current_room_id) : null;
      const requestedRoom = db.rooms.find(r => r.id === request.requested_room_id);
      
      return {
        ...request,
        student_name: student ? student.full_name : 'Unknown',
        current_room: currentRoom ? currentRoom.room_number : null,
        requested_room: requestedRoom ? requestedRoom.room_number : 'Unknown'
      };
    }).sort((a, b) => new Date(b.requested_at) - new Date(a.requested_at));

    res.json(requestsWithDetails);
  } catch (error) {
    console.error('Get room change requests error:', error);
    res.status(500).json({ error: 'Database error' });
  }
});

// Get student's current room info
app.get('/api/student/my-room', authenticateToken, (req, res) => {
  if (req.user.role !== 'student') {
    return res.status(403).json({ error: 'Access denied' });
  }

  try {
    const myBed = db.beds.find(bed => bed.student_id === req.user.id);
    
    if (!myBed) {
      return res.status(404).json({ error: 'No room assigned' });
    }

    const room = db.rooms.find(r => r.id === myBed.room_id);
    const roommates = db.beds
      .filter(bed => bed.room_id === myBed.room_id && bed.student_id && bed.student_id !== req.user.id)
      .map(bed => {
        const student = db.users.find(u => u.id === bed.student_id);
        return { full_name: student ? student.full_name : 'Unknown' };
      });

    res.json({ 
      ...room, 
      bed_number: myBed.bed_number,
      roommates 
    });
  } catch (error) {
    console.error('Get my room error:', error);
    res.status(500).json({ error: 'Database error' });
  }
});

// Change password
app.post('/api/change-password', authenticateToken, (req, res) => {
  try {
    const { currentPassword, newPassword } = req.body;

    if (!currentPassword || !newPassword) {
      return res.status(400).json({ error: 'Both current and new passwords are required' });
    }

    const user = db.users.find(u => u.id === req.user.id);
    if (!user) {
      return res.status(404).json({ error: 'User not found' });
    }

    // Verify current password
    const isCurrentPasswordValid = bcrypt.compareSync(currentPassword, user.password);
    if (!isCurrentPasswordValid) {
      return res.status(400).json({ error: 'Current password is incorrect' });
    }

    // Hash new password
    const hashedNewPassword = bcrypt.hashSync(newPassword, 10);
    user.password = hashedNewPassword;
    user.updated_at = new Date().toISOString();

    saveDatabase();

    res.json({ message: 'Password changed successfully' });
  } catch (error) {
    console.error('Change password error:', error);
    res.status(500).json({ error: 'Failed to change password' });
  }
});

// Get all students (for warden)
app.get('/api/warden/students', authenticateToken, (req, res) => {
  if (req.user.role !== 'warden') {
    return res.status(403).json({ error: 'Access denied' });
  }

  try {
    const students = db.users.filter(u => u.role === 'student').map(student => {
      // Find student's current bed assignment
      const bed = db.beds.find(b => b.student_id === student.id);
      const room = bed ? db.rooms.find(r => r.id === bed.room_id) : null;

      return {
        ...student,
        password: undefined, // Don't send password hash
        room_number: room ? room.room_number : null,
        room_id: room ? room.id : null,
        bed_number: bed ? bed.bed_number : null,
        floor: room ? room.floor : null
      };
    });

    res.json(students);
  } catch (error) {
    console.error('Get students error:', error);
    res.status(500).json({ error: 'Database error' });
  }
});

// Get warden contact information (for students)
app.get('/api/student/warden-contact', authenticateToken, (req, res) => {
  if (req.user.role !== 'student') {
    return res.status(403).json({ error: 'Access denied' });
  }

  try {
    const warden = db.users.find(u => u.role === 'warden');
    if (!warden) {
      return res.status(404).json({ error: 'Warden contact not found' });
    }

    res.json({
      name: warden.full_name,
      email: warden.email,
      phone: warden.phone,
      emergency_contact: warden.phone, // Using same phone as emergency for now
      office_hours: 'Monday - Friday: 9:00 AM - 5:00 PM'
    });
  } catch (error) {
    console.error('Get warden contact error:', error);
    res.status(500).json({ error: 'Database error' });
  }
});

// Handle room change request action (approve/reject)
app.put('/api/warden/room-change-requests/:id/:action', authenticateToken, (req, res) => {
  if (req.user.role !== 'warden') {
    return res.status(403).json({ error: 'Access denied' });
  }

  try {
    const requestId = parseInt(req.params.id);
    const action = req.params.action;
    const { comments } = req.body;

    if (!['approve', 'reject'].includes(action)) {
      return res.status(400).json({ error: 'Invalid action' });
    }

    const request = db.room_change_requests.find(r => r.id === requestId);
    if (!request) {
      return res.status(404).json({ error: 'Request not found' });
    }

    if (request.status !== 'pending') {
      return res.status(400).json({ error: 'Request already processed' });
    }

    // Update request status
    request.status = action === 'approve' ? 'approved' : 'rejected';
    request.processed_at = new Date().toISOString();
    request.processed_by = req.user.id;
    request.comments = comments || null;

    // If approved, handle the room change
    if (action === 'approve') {
      // Find student's current bed
      const currentBed = db.beds.find(b => b.student_id === request.student_id);
      
      // Find requested bed
      const requestedBed = db.beds.find(b => 
        b.room_id === request.requested_room_id && 
        b.bed_number === request.requested_bed_number
      );

      if (!requestedBed) {
        return res.status(400).json({ error: 'Requested bed not found' });
      }

      if (requestedBed.status === 'occupied') {
        return res.status(400).json({ error: 'Requested bed is no longer available' });
      }

      // Free up current bed
      if (currentBed) {
        currentBed.student_id = null;
        currentBed.status = 'available';
        currentBed.updated_at = new Date().toISOString();
      }

      // Assign new bed
      requestedBed.student_id = request.student_id;
      requestedBed.status = 'occupied';
      requestedBed.updated_at = new Date().toISOString();
    }

    saveDatabase();

    res.json({ 
      message: `Room change request ${action}d successfully`,
      request: {
        ...request,
        student_name: db.users.find(u => u.id === request.student_id)?.full_name || 'Unknown'
      }
    });
  } catch (error) {
    console.error('Process room change request error:', error);
    res.status(500).json({ error: 'Failed to process request' });
  }
});

// Submit personal details update request
app.post('/api/student/personal-details-update-request', authenticateToken, (req, res) => {
  if (req.user.role !== 'student') {
    return res.status(403).json({ error: 'Access denied' });
  }

  try {
    const {
      phone,
      address_line1,
      address_line2,
      city,
      state,
      postal_code,
      guardian_name,
      guardian_phone,
      guardian_address
    } = req.body;

    const requestId = getNextId('personal_details_requests');

    const newRequest = {
      id: requestId,
      student_id: req.user.id,
      phone,
      address_line1,
      address_line2,
      city,
      state,
      postal_code,
      guardian_name,
      guardian_phone,
      guardian_address,
      status: 'pending',
      requested_at: new Date().toISOString(),
      processed_at: null,
      processed_by: null,
      comments: null
    };

    db.personal_details_requests.push(newRequest);
    saveDatabase();

    res.json({ message: 'Personal details update request submitted successfully' });
  } catch (error) {
    console.error('Submit personal details request error:', error);
    res.status(500).json({ error: 'Failed to submit request' });
  }
});

// Get personal details update requests (for warden)
app.get('/api/warden/personal-details-update-requests', authenticateToken, (req, res) => {
  if (req.user.role !== 'warden') {
    return res.status(403).json({ error: 'Access denied' });
  }

  try {
    const requestsWithDetails = db.personal_details_requests.map(request => {
      const student = db.users.find(u => u.id === request.student_id);
      
      return {
        ...request,
        student_name: student ? student.full_name : 'Unknown',
        student_roll_number: student ? student.username : 'Unknown'
      };
    }).sort((a, b) => new Date(b.requested_at) - new Date(a.requested_at));

    res.json(requestsWithDetails);
  } catch (error) {
    console.error('Get personal details requests error:', error);
    res.status(500).json({ error: 'Database error' });
  }
});

// Handle personal details update request action (approve/reject)
app.put('/api/warden/personal-details-update-requests/:id/:action', authenticateToken, (req, res) => {
  if (req.user.role !== 'warden') {
    return res.status(403).json({ error: 'Access denied' });
  }

  try {
    const requestId = parseInt(req.params.id);
    const action = req.params.action;
    const { comments } = req.body;

    if (!['approve', 'reject'].includes(action)) {
      return res.status(400).json({ error: 'Invalid action' });
    }

    const request = db.personal_details_requests.find(r => r.id === requestId);
    if (!request) {
      return res.status(404).json({ error: 'Request not found' });
    }

    if (request.status !== 'pending') {
      return res.status(400).json({ error: 'Request already processed' });
    }

    // Update request status
    request.status = action === 'approve' ? 'approved' : 'rejected';
    request.processed_at = new Date().toISOString();
    request.processed_by = req.user.id;
    request.comments = comments || null;

    // If approved, update student's profile
    if (action === 'approve') {
      const student = db.users.find(u => u.id === request.student_id);
      if (student) {
        student.phone = request.phone || student.phone;
        student.address_line1 = request.address_line1 || student.address_line1;
        student.address_line2 = request.address_line2 || student.address_line2;
        student.city = request.city || student.city;
        student.state = request.state || student.state;
        student.postal_code = request.postal_code || student.postal_code;
        student.guardian_name = request.guardian_name || student.guardian_name;
        student.guardian_phone = request.guardian_phone || student.guardian_phone;
        student.guardian_address = request.guardian_address || student.guardian_address;
        student.updated_at = new Date().toISOString();
      }
    }

    saveDatabase();

    res.json({ 
      message: `Personal details request ${action}d successfully`,
      request: {
        ...request,
        student_name: db.users.find(u => u.id === request.student_id)?.full_name || 'Unknown'
      }
    });
  } catch (error) {
    console.error('Process personal details request error:', error);
    res.status(500).json({ error: 'Failed to process request' });
  }
});

app.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
  console.log(`Default warden login: username: warden, password: warden123`);
}); 