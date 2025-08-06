// MongoDB initialization script
print('Starting MongoDB initialization...');

// Switch to hostel_management database
db = db.getSiblingDB('hostel_management');

// Create collections
db.createCollection('users');
db.createCollection('rooms');
db.createCollection('beds');
db.createCollection('food_menu');
db.createCollection('room_change_requests');
db.createCollection('personal_details_requests');

// Create indexes for better performance
db.users.createIndex({ "username": 1 }, { unique: true });
db.users.createIndex({ "email": 1 });
db.users.createIndex({ "role": 1 });

db.rooms.createIndex({ "room_number": 1 }, { unique: true });
db.rooms.createIndex({ "floor": 1 });

db.beds.createIndex({ "room_id": 1, "bed_number": 1 }, { unique: true });
db.beds.createIndex({ "student_id": 1 });
db.beds.createIndex({ "status": 1 });

db.food_menu.createIndex({ "day_of_week": 1, "meal_type": 1 });

db.room_change_requests.createIndex({ "student_id": 1 });
db.room_change_requests.createIndex({ "status": 1 });

db.personal_details_requests.createIndex({ "student_id": 1 });
db.personal_details_requests.createIndex({ "status": 1 });

// Insert sample data
print('Inserting sample data...');

// Sample warden user
db.users.insertOne({
  username: "warden",
  password: "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi", // password: warden123
  role: "warden",
  full_name: "Hostel Warden",
  email: "warden@hostel.edu",
  phone: "9876543210",
  created_at: new Date()
});

// Sample rooms
for (let i = 1; i <= 10; i++) {
  const roomNumber = `R${i.toString().padStart(3, '0')}`;
  const floor = Math.ceil(i / 4);
  
  db.rooms.insertOne({
    room_number: roomNumber,
    floor: floor,
    capacity: 3,
    occupied_beds: 0,
    room_type: "standard",
    created_at: new Date()
  });
}

// Sample beds (3 beds per room)
let bedCounter = 1;
for (let roomId = 1; roomId <= 10; roomId++) {
  for (let bedNum = 1; bedNum <= 3; bedNum++) {
    db.beds.insertOne({
      room_id: roomId,
      bed_number: bedNum,
      student_id: null,
      status: "available",
      created_at: new Date()
    });
    bedCounter++;
  }
}

// Sample food menu
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

db.food_menu.insertMany(menuItems.map(item => ({
  ...item,
  created_at: new Date()
})));

print('MongoDB initialization completed successfully!');
print('Database: hostel_management');
print('Collections created: users, rooms, beds, food_menu, room_change_requests, personal_details_requests');
print('Sample data inserted for warden, rooms, beds, and food menu');
print('Default login: username=warden, password=warden123'); 