import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import './WardenDashboard.css';

const WardenDashboard = () => {
  const { user, logout, apiCall } = useAuth();
  const [activeTab, setActiveTab] = useState('overview');
  const [rooms, setRooms] = useState([]);
  const [students, setStudents] = useState([]);
  const [roomChangeRequests, setRoomChangeRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');
  
  // Form states
  const [newStudent, setNewStudent] = useState({
    full_name: '',
    email: '',
    phone: '',
    date_of_birth: '',
    gender: '',
    aadhaar_id: '',
    roll_no: '',
    stream: '',
    branch: ''
  });
  
  const [roomAssignment, setRoomAssignment] = useState({
    student_id: '',
    room_id: '',
    bed_number: ''
  });
  
  const [selectedRoom, setSelectedRoom] = useState(null);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    
    // Fetch rooms
    const roomsResult = await apiCall('GET', '/api/rooms');
    if (roomsResult.success) {
      setRooms(roomsResult.data);
    }

    // Fetch room change requests
    const requestsResult = await apiCall('GET', '/api/warden/room-change-requests');
    if (requestsResult.success) {
      setRoomChangeRequests(requestsResult.data);
    }

    setLoading(false);
  };

  const handleCreateStudent = async (e) => {
    e.preventDefault();
    
    // Client-side validation
    if (newStudent.phone && !/^[6-9][0-9]{9}$/.test(newStudent.phone)) {
      setMessage('Error: Please enter a valid 10-digit Indian mobile number starting with 6, 7, 8, or 9');
      setTimeout(() => setMessage(''), 5000);
      return;
    }
    
    const result = await apiCall('POST', '/api/warden/create-student', newStudent);
    
    if (result.success) {
      const student = result.data.student;
      const credentials = result.data.credentials;
      
      setMessage(`Student created successfully! 
        
Student Details:
- Name: ${student.full_name}
- Roll No: ${student.roll_no}
- Stream: ${student.stream}
- Branch: ${student.branch}

Login Credentials:
- Username: ${credentials.username} (Roll Number)
- Password: ${credentials.password}

Please share these credentials with the student.`);
      
      setNewStudent({ 
        full_name: '', 
        email: '', 
        phone: '',
        date_of_birth: '',
        gender: '',
        aadhaar_id: '',
        roll_no: '',
        stream: '',
        branch: ''
      });
    } else {
      setMessage(`Error: ${result.error}`);
    }

    setTimeout(() => setMessage(''), 15000);
  };

  const handleRoomAssignment = async (e) => {
    e.preventDefault();
    
    const result = await apiCall('POST', '/api/warden/assign-room', roomAssignment);
    
    if (result.success) {
      setMessage('Room assigned successfully!');
      setRoomAssignment({ student_id: '', room_id: '', bed_number: '' });
      fetchData(); // Refresh data
    } else {
      setMessage(`Error: ${result.error}`);
    }

    setTimeout(() => setMessage(''), 5000);
  };

  const handleRoomDetails = async (roomId) => {
    const result = await apiCall('GET', `/api/rooms/${roomId}`);
    if (result.success) {
      setSelectedRoom(result.data);
    }
  };

  const getAvailableBeds = (roomId) => {
    const room = rooms.find(r => r.id === parseInt(roomId));
    if (!room) return [];
    
    const beds = [];
    for (let i = 1; i <= room.capacity; i++) {
      beds.push(i);
    }
    return beds;
  };

  const getRoomStatistics = () => {
    const totalRooms = rooms.length;
    const occupiedRooms = rooms.filter(room => room.occupied_beds > 0).length;
    const totalBeds = rooms.reduce((sum, room) => sum + room.capacity, 0);
    const occupiedBeds = rooms.reduce((sum, room) => sum + room.occupied_beds, 0);
    
    return { totalRooms, occupiedRooms, totalBeds, occupiedBeds };
  };

  if (loading) {
    return <div className="loading">Loading dashboard...</div>;
  }

  const stats = getRoomStatistics();

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="header-content">
          <h1>Warden Dashboard</h1>
          <div className="user-info">
            <span>Welcome, {user.full_name}</span>
            <button onClick={logout} className="logout-button">Logout</button>
          </div>
        </div>
      </header>

      <div className="dashboard-content">
        <nav className="dashboard-nav">
          <button 
            className={`nav-button ${activeTab === 'overview' ? 'active' : ''}`}
            onClick={() => setActiveTab('overview')}
          >
            Overview
          </button>
          <button 
            className={`nav-button ${activeTab === 'students' ? 'active' : ''}`}
            onClick={() => setActiveTab('students')}
          >
            Create Student
          </button>
          <button 
            className={`nav-button ${activeTab === 'rooms' ? 'active' : ''}`}
            onClick={() => setActiveTab('rooms')}
          >
            Manage Rooms
          </button>
          <button 
            className={`nav-button ${activeTab === 'assign' ? 'active' : ''}`}
            onClick={() => setActiveTab('assign')}
          >
            Assign Rooms
          </button>
          <button 
            className={`nav-button ${activeTab === 'requests' ? 'active' : ''}`}
            onClick={() => setActiveTab('requests')}
          >
            Change Requests
          </button>
        </nav>

        <main className="dashboard-main">
          {message && <div className="message">{message}</div>}

          {activeTab === 'overview' && (
            <div className="overview-section">
              <h2>Hostel Overview</h2>
              <div className="stats-grid">
                <div className="stat-card">
                  <h3>Total Rooms</h3>
                  <div className="stat-number">{stats.totalRooms}</div>
                </div>
                <div className="stat-card">
                  <h3>Occupied Rooms</h3>
                  <div className="stat-number">{stats.occupiedRooms}</div>
                </div>
                <div className="stat-card">
                  <h3>Total Beds</h3>
                  <div className="stat-number">{stats.totalBeds}</div>
                </div>
                <div className="stat-card">
                  <h3>Occupied Beds</h3>
                  <div className="stat-number">{stats.occupiedBeds}</div>
                </div>
              </div>
              
              <div className="quick-info">
                <h3>Room Occupancy Status</h3>
                <div className="rooms-grid">
                  {rooms.slice(0, 10).map(room => (
                    <div key={room.id} className="room-status-card">
                      <h4>Room {room.room_number}</h4>
                      <p>Floor {room.floor}</p>
                      <div className="occupancy">
                        {room.occupied_beds}/{room.capacity} beds occupied
                      </div>
                      <div className={`status ${room.occupied_beds === room.capacity ? 'full' : room.occupied_beds > 0 ? 'partial' : 'empty'}`}>
                        {room.occupied_beds === room.capacity ? 'Full' : room.occupied_beds > 0 ? 'Partial' : 'Empty'}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {activeTab === 'students' && (
            <div className="students-section">
              <h2>Create Student Account</h2>
              <form onSubmit={handleCreateStudent} className="student-form">
                
                {/* Personal Information Section */}
                <div className="form-section">
                  <h3 className="form-section-title">Personal Information</h3>
                  
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="full_name">Full Name *</label>
                      <input
                        type="text"
                        id="full_name"
                        value={newStudent.full_name}
                        onChange={(e) => setNewStudent({
                          ...newStudent,
                          full_name: e.target.value
                        })}
                        required
                        placeholder="Enter full name"
                      />
                    </div>
                    
                    <div className="form-group">
                      <label htmlFor="date_of_birth">Date of Birth *</label>
                      <input
                        type="date"
                        id="date_of_birth"
                        value={newStudent.date_of_birth}
                        onChange={(e) => setNewStudent({
                          ...newStudent,
                          date_of_birth: e.target.value
                        })}
                        required
                        max={new Date().toISOString().split('T')[0]}
                      />
                    </div>
                  </div>

                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="gender">Gender *</label>
                      <select
                        id="gender"
                        value={newStudent.gender}
                        onChange={(e) => setNewStudent({
                          ...newStudent,
                          gender: e.target.value
                        })}
                        required
                      >
                        <option value="">Select Gender</option>
                        <option value="Male">Male</option>
                        <option value="Female">Female</option>
                        <option value="Other">Other</option>
                      </select>
                    </div>
                    
                    <div className="form-group">
                      <label htmlFor="aadhaar_id">Aadhaar ID *</label>
                      <input
                        type="text"
                        id="aadhaar_id"
                        value={newStudent.aadhaar_id}
                        onChange={(e) => setNewStudent({
                          ...newStudent,
                          aadhaar_id: e.target.value.replace(/\D/g, '').slice(0, 12)
                        })}
                        required
                        placeholder="Enter 12-digit Aadhaar ID"
                        pattern="[0-9]{12}"
                        maxLength="12"
                      />
                    </div>
                  </div>
                </div>

                {/* Contact Information Section */}
                <div className="form-section">
                  <h3 className="form-section-title">Contact Information</h3>
                  
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="email">Email Address</label>
                      <input
                        type="email"
                        id="email"
                        value={newStudent.email}
                        onChange={(e) => setNewStudent({
                          ...newStudent,
                          email: e.target.value
                        })}
                        placeholder="Enter email address"
                      />
                    </div>
                    
                    <div className="form-group">
                      <label htmlFor="phone">Phone Number *</label>
                      <input
                        type="tel"
                        id="phone"
                        value={newStudent.phone}
                        onChange={(e) => {
                          const value = e.target.value.replace(/\D/g, '').slice(0, 10);
                          setNewStudent({
                            ...newStudent,
                            phone: value
                          });
                        }}
                        required
                        placeholder="Enter 10-digit mobile number"
                        pattern="^[6-9][0-9]{9}$"
                        maxLength="10"
                        title="Please enter a valid 10-digit Indian mobile number starting with 6, 7, 8, or 9"
                      />
                      {newStudent.phone && newStudent.phone.length > 0 && newStudent.phone.length < 10 && (
                        <span className="validation-error">Phone number must be 10 digits</span>
                      )}
                      {newStudent.phone && newStudent.phone.length === 10 && !/^[6-9]/.test(newStudent.phone) && (
                        <span className="validation-error">Phone number must start with 6, 7, 8, or 9</span>
                      )}
                      {newStudent.phone && newStudent.phone.length === 10 && /^[6-9][0-9]{9}$/.test(newStudent.phone) && (
                        <span className="validation-success">✓ Valid phone number</span>
                      )}
                    </div>
                  </div>
                </div>

                {/* Academic Information Section */}
                <div className="form-section">
                  <h3 className="form-section-title">Academic Information</h3>
                  
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="roll_no">Roll Number *</label>
                      <input
                        type="text"
                        id="roll_no"
                        value={newStudent.roll_no}
                        onChange={(e) => setNewStudent({
                          ...newStudent,
                          roll_no: e.target.value.toUpperCase()
                        })}
                        required
                        placeholder="Enter roll number (will be used as username)"
                      />
                      <small className="field-note">Note: Roll number will be used as the login username</small>
                    </div>
                    
                    <div className="form-group">
                      <label htmlFor="stream">Stream *</label>
                      <select
                        id="stream"
                        value={newStudent.stream}
                        onChange={(e) => setNewStudent({
                          ...newStudent,
                          stream: e.target.value
                        })}
                        required
                      >
                        <option value="">Select Stream</option>
                        <option value="Engineering">Engineering</option>
                        <option value="Medical">Medical</option>
                        <option value="Commerce">Commerce</option>
                        <option value="Arts">Arts</option>
                        <option value="Science">Science</option>
                        <option value="Management">Management</option>
                      </select>
                    </div>
                  </div>

                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="branch">Branch *</label>
                      <input
                        type="text"
                        id="branch"
                        value={newStudent.branch}
                        onChange={(e) => setNewStudent({
                          ...newStudent,
                          branch: e.target.value
                        })}
                        required
                        placeholder="Enter branch/specialization"
                      />
                    </div>
                  </div>
                </div>

                <div className="form-actions">
                  <button type="submit" className="submit-button">
                    Create Student Account
                  </button>
                  <button 
                    type="button" 
                    className="reset-button"
                    onClick={() => setNewStudent({
                      full_name: '',
                      email: '',
                      phone: '',
                      date_of_birth: '',
                      gender: '',
                      aadhaar_id: '',
                      roll_no: '',
                      stream: '',
                      branch: ''
                    })}
                  >
                    Reset Form
                  </button>
                </div>
              </form>
            </div>
          )}

          {activeTab === 'rooms' && (
            <div className="rooms-section">
              <h2>Room Management</h2>
              <div className="rooms-grid">
                {rooms.map(room => (
                  <div key={room.id} className="room-card">
                    <div className="room-header">
                      <h3>Room {room.room_number}</h3>
                      <span className="floor-badge">Floor {room.floor}</span>
                    </div>
                    <div className="room-info">
                      <p><strong>Capacity:</strong> {room.capacity} beds</p>
                      <p><strong>Occupied:</strong> {room.occupied_beds} beds</p>
                      <p><strong>Available:</strong> {room.available_beds} beds</p>
                    </div>
                    <button 
                      onClick={() => handleRoomDetails(room.id)}
                      className="details-button"
                    >
                      View Details
                    </button>
                  </div>
                ))}
              </div>

              {selectedRoom && (
                <div className="room-details-modal">
                  <div className="modal-content">
                    <h3>Room {selectedRoom.room_number} Details</h3>
                    <div className="beds-info">
                      <h4>Bed Assignment:</h4>
                      {selectedRoom.beds?.map(bed => (
                        <div key={bed.id} className="bed-info">
                          <span>Bed {bed.bed_number}: </span>
                          <span className={bed.status}>
                            {bed.status === 'occupied' ? `${bed.student_name}` : bed.status}
                          </span>
                        </div>
                      ))}
                    </div>
                    <button onClick={() => setSelectedRoom(null)} className="close-button">
                      Close
                    </button>
                  </div>
                </div>
              )}
            </div>
          )}

          {activeTab === 'assign' && (
            <div className="assign-section">
              <h2>Assign Room to Student</h2>
              <form onSubmit={handleRoomAssignment} className="assign-form">
                <div className="form-group">
                  <label htmlFor="student_id">Student ID</label>
                  <input
                    type="number"
                    id="student_id"
                    value={roomAssignment.student_id}
                    onChange={(e) => setRoomAssignment({
                      ...roomAssignment,
                      student_id: e.target.value
                    })}
                    required
                    placeholder="Enter student ID"
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="room_select">Select Room</label>
                  <select
                    id="room_select"
                    value={roomAssignment.room_id}
                    onChange={(e) => setRoomAssignment({
                      ...roomAssignment,
                      room_id: e.target.value,
                      bed_number: ''
                    })}
                    required
                  >
                    <option value="">Choose a room...</option>
                    {rooms
                      .filter(room => room.available_beds > 0)
                      .map(room => (
                        <option key={room.id} value={room.id}>
                          Room {room.room_number} - Floor {room.floor} 
                          ({room.available_beds} beds available)
                        </option>
                      ))}
                  </select>
                </div>

                {roomAssignment.room_id && (
                  <div className="form-group">
                    <label htmlFor="bed_select">Select Bed</label>
                    <select
                      id="bed_select"
                      value={roomAssignment.bed_number}
                      onChange={(e) => setRoomAssignment({
                        ...roomAssignment,
                        bed_number: e.target.value
                      })}
                      required
                    >
                      <option value="">Choose a bed...</option>
                      {getAvailableBeds(roomAssignment.room_id).map(bedNumber => (
                        <option key={bedNumber} value={bedNumber}>
                          Bed {bedNumber}
                        </option>
                      ))}
                    </select>
                  </div>
                )}

                <button type="submit" className="submit-button">
                  Assign Room
                </button>
              </form>
            </div>
          )}

          {activeTab === 'requests' && (
            <div className="requests-section">
              <h2>Room Change Requests</h2>
              {roomChangeRequests.length === 0 ? (
                <p>No room change requests at this time.</p>
              ) : (
                <div className="requests-list">
                  {roomChangeRequests.map(request => (
                    <div key={request.id} className="request-card">
                      <div className="request-header">
                        <h4>{request.student_name}</h4>
                        <span className={`status-badge ${request.status}`}>
                          {request.status}
                        </span>
                      </div>
                      <div className="request-details">
                        <p><strong>Current Room:</strong> {request.current_room || 'Not assigned'}</p>
                        <p><strong>Requested Room:</strong> {request.requested_room}</p>
                        <p><strong>Reason:</strong> {request.reason}</p>
                        <p><strong>Requested Date:</strong> {new Date(request.requested_at).toLocaleDateString()}</p>
                      </div>
                      {request.status === 'pending' && (
                        <div className="request-actions">
                          <button className="approve-button">Approve</button>
                          <button className="reject-button">Reject</button>
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default WardenDashboard; 