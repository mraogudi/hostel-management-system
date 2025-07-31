import React, { useState, useEffect } from 'react';
import axios from 'axios';
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
    branch: '',
    // Address fields
    address_line1: '',
    address_line2: '',
    city: '',
    state: '',
    postal_code: '',
    // Guardian fields
    guardian_name: '',
    guardian_address: '',
    guardian_phone: ''
  });
  
  const [roomAssignment, setRoomAssignment] = useState({
    studentId: '',
    roomId: '',
    bedNumber: ''
  });
  
  const [availableBeds, setAvailableBeds] = useState([]);
  const [bedsLoading, setBedsLoading] = useState(false);
  
  // Student Management states
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [showStudentModal, setShowStudentModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [editingStudent, setEditingStudent] = useState(null);
  const [deleteConfirm, setDeleteConfirm] = useState(null);
  const [passwordReset, setPasswordReset] = useState(null);
  const [studentsLoading, setStudentsLoading] = useState(false);

  // Room change request states
  const [processingRequest, setProcessingRequest] = useState(null);
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [confirmAction, setConfirmAction] = useState({ type: '', request: null });

  const [selectedRoom, setSelectedRoom] = useState(null);

  useEffect(() => {
    fetchData();
  }, []);

  // Additional useEffect to fetch students when students tab is activated
  useEffect(() => {
    if (activeTab === 'students' && students.length === 0) {
      console.log('Students tab activated, fetching students...');
      fetchStudentsData();
    }
  }, [activeTab]);

  const fetchStudentsData = async () => {
    console.log('Fetching students data specifically...');
    const studentsResult = await apiCall('GET', '/api/warden/students');
    console.log('Students API response:', studentsResult);
    
    if (studentsResult.success) {
      setStudents(studentsResult.data);
      console.log('Students data loaded:', studentsResult.data);
    } else {
      console.error('Failed to fetch students:', studentsResult.error);
      setMessage(`Error fetching students: ${studentsResult.error}`);
    }
  };

  const fetchData = async () => {
    console.log('=== fetchData called ===');
    setLoading(true);
    
    try {
      // Fetch rooms
      console.log('Fetching rooms...');
      const roomsResult = await apiCall('GET', '/api/rooms');
      if (roomsResult.success) {
        setRooms(roomsResult.data);
        console.log('Rooms fetched:', roomsResult.data.length, 'rooms');
      } else {
        console.error('Failed to fetch rooms:', roomsResult.error);
      }

      // Fetch room change requests
      console.log('Fetching room change requests...');
      const requestsResult = await apiCall('GET', '/api/warden/room-change-requests');
      if (requestsResult.success) {
        setRoomChangeRequests(requestsResult.data);
        console.log('Room change requests fetched:', requestsResult.data.length, 'requests');
      } else {
        console.error('Failed to fetch room change requests:', requestsResult.error);
      }

      // Fetch students list
      console.log('Fetching students...');
      const studentsResult = await apiCall('GET', '/api/warden/students');
      console.log('Students API response:', studentsResult);
      
      if (studentsResult.success) {
        setStudents(studentsResult.data);
        console.log('Students fetched successfully:', studentsResult.data.length, 'students');
        console.log('Sample student data:', studentsResult.data[0]);
      } else {
        console.error('Failed to fetch students:', studentsResult.error);
        setMessage(`Error fetching students: ${studentsResult.error}`);
      }
    } catch (error) {
      console.error('Error in fetchData:', error);
      setMessage('Error loading dashboard data');
    }
    
    setLoading(false);
    console.log('=== fetchData completed ===');
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
        branch: '',
        // Address fields
        address_line1: '',
        address_line2: '',
        city: '',
        state: '',
        postal_code: '',
        // Guardian fields
        guardian_name: '',
        guardian_address: '',
        guardian_phone: ''
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
      setRoomAssignment({ studentId: '', roomId: '', bedNumber: '' });
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

  const getAvailableBeds = async (roomId) => {
    if (!roomId) return [];
    
    try {
      console.log('=== getAvailableBeds called ===');
      console.log('Room ID:', roomId);
      
      // Fetch detailed room information including bed availability
      const result = await apiCall('GET', `/api/rooms/${roomId}`);
      console.log('API response success:', result.success);
      console.log('Full API response:', JSON.stringify(result, null, 2));
      
      if (result.success && result.data) {
        const roomData = result.data;
        console.log('Room number:', roomData.room_number);
        console.log('Room capacity:', roomData.capacity);
        console.log('Beds data type:', typeof roomData.beds);
        console.log('Beds is array:', Array.isArray(roomData.beds));
        console.log('Beds length:', roomData.beds ? roomData.beds.length : 'undefined');
        
        if (roomData.beds && Array.isArray(roomData.beds)) {
          console.log('Processing beds array:');
          roomData.beds.forEach((bed, index) => {
            console.log(`  Bed ${index + 1}:`, {
              bed_number: bed.bed_number,
              status: bed.status,
              student_id: bed.student_id
            });
          });
          
          // Return only available bed numbers
          const availableBeds = roomData.beds
            .filter(bed => {
              const isAvailable = bed.status === 'available';
              console.log(`  Bed ${bed.bed_number}: ${bed.status} -> ${isAvailable ? 'AVAILABLE' : 'NOT AVAILABLE'}`);
              return isAvailable;
            })
            .map(bed => bed.bed_number)
            .sort((a, b) => a - b);
          
          console.log('Final available beds array:', availableBeds);
          console.log('=== getAvailableBeds completed ===');
          return availableBeds;
        } else {
          console.log('ERROR: No valid beds array found');
          console.log('beds value:', roomData.beds);
          return [];
        }
      } else {
        console.log('ERROR: API call failed or no data');
        console.log('Result success:', result.success);
        console.log('Result data:', result.data);
        console.log('Result error:', result.error);
        return [];
      }
    } catch (error) {
      console.error('EXCEPTION in getAvailableBeds:', error);
      return [];
    }
  };

  // Handle room selection and fetch available beds
  const handleRoomSelection = async (roomId) => {
    setRoomAssignment({
      ...roomAssignment,
      roomId: roomId,
      bedNumber: ''
    });

    if (roomId) {
      setBedsLoading(true);
      const beds = await getAvailableBeds(roomId);
      setAvailableBeds(beds);
      setBedsLoading(false);
    } else {
      setAvailableBeds([]);
      setBedsLoading(false);
    }
  };

  const getRoomStatistics = () => {
    const totalRooms = rooms.length;
    const occupiedRooms = rooms.filter(room => room.occupied_beds > 0).length;
    const totalBeds = rooms.reduce((sum, room) => sum + room.capacity, 0);
    const occupiedBeds = rooms.reduce((sum, room) => sum + room.occupied_beds, 0);
    
    return { totalRooms, occupiedRooms, totalBeds, occupiedBeds };
  };

  // Handle room change request approval/rejection
  const handleRequestAction = (request, action) => {
    setConfirmAction({ type: action, request });
    setShowConfirmModal(true);
  };

  const confirmRequestAction = async () => {
    if (!confirmAction.request || !confirmAction.type) return;

    setProcessingRequest(confirmAction.request.id);
    
    try {
      const endpoint = `/api/warden/room-change-requests/${confirmAction.request.id}/${confirmAction.type}`;
      const result = await apiCall('PUT', endpoint, {});
      
      if (result.success) {
        setMessage(`Room change request ${confirmAction.type}d successfully!`);
        fetchData(); // Refresh the data
      } else {
        setMessage(`Error: ${result.error}`);
      }
    } catch (error) {
      setMessage(`Error: Failed to ${confirmAction.type} request`);
    }
    
    setProcessingRequest(null);
    setShowConfirmModal(false);
    setConfirmAction({ type: '', request: null });
    setTimeout(() => setMessage(''), 5000);
  };

  const cancelRequestAction = () => {
    setShowConfirmModal(false);
    setConfirmAction({ type: '', request: null });
  };

  // Student management functions
  const handleEditStudent = (student) => {
    setEditingStudent({ ...student });
    setShowEditModal(true);
  };

  const handleViewStudent = (student) => {
    setSelectedStudent(student);
    setShowStudentModal(true);
  };

  const handleCloseStudentModal = () => {
    setShowStudentModal(false);
    setSelectedStudent(null);
  };

  const handleDeleteConfirm = (student) => {
    setDeleteConfirm(student);
  };

  const handleDeleteStudent = async () => {
    if (!deleteConfirm) return;

    setStudentsLoading(true);
    
    const result = await apiCall('DELETE', `/api/warden/students/${deleteConfirm.id}`);
    
    if (result.success) {
      setMessage('Student deleted successfully!');
      fetchData(); // Refresh data
    } else {
      setMessage(`Error: ${result.error}`);
    }
    
    setDeleteConfirm(null);
    setStudentsLoading(false);
    setTimeout(() => setMessage(''), 5000);
  };

  const handleSaveEdit = async (e) => {
    e.preventDefault();
    
    setStudentsLoading(true);
    
    const result = await apiCall('PUT', `/api/warden/students/${editingStudent.id}`, editingStudent);
    
    if (result.success) {
      setMessage('Student updated successfully!');
      setShowEditModal(false);
      setEditingStudent(null);
      fetchData(); // Refresh data
    } else {
      setMessage(`Error: ${result.error}`);
    }
    
    setStudentsLoading(false);
    setTimeout(() => setMessage(''), 5000);
  };

  const handleCancelEdit = () => {
    setShowEditModal(false);
    setEditingStudent(null);
  };

  const handleCancelDelete = () => {
    setDeleteConfirm(null);
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
            className={`nav-button ${activeTab === 'create' ? 'active' : ''}`}
            onClick={() => setActiveTab('create')}
          >
            Add Student
          </button>
          <button 
            className={`nav-button ${activeTab === 'students' ? 'active' : ''}`}
            onClick={() => setActiveTab('students')}
          >
            Students
          </button>
          <button 
            className={`nav-button ${activeTab === 'assign' ? 'active' : ''}`}
            onClick={() => setActiveTab('assign')}
          >
            Assign Room
          </button>
          <button 
            className={`nav-button ${activeTab === 'requests' ? 'active' : ''}`}
            onClick={() => setActiveTab('requests')}
          >
            Requests
          </button>
        </nav>

        <main className="dashboard-main">
          {message && <div className="message">{message}</div>}

          {activeTab === 'overview' && (
            <div className="overview-section">
              <div className="section-header">
                <h2>Hostel Overview</h2>
                <button 
                  className="refresh-btn"
                  onClick={() => fetchData()}
                  title="Refresh Overview Data"
                >
                  🔄
                </button>
              </div>
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

          {activeTab === 'create' && (
            <div className="create-student-section">
              <div className="section-header">
                <h2>Create Student Account</h2>
                <button 
                  className="refresh-btn"
                  onClick={() => setNewStudent({
                    full_name: '', email: '', phone: '', date_of_birth: '', gender: '',
                    aadhaar_id: '', roll_no: '', stream: '', branch: '',
                    address_line1: '', address_line2: '', city: '', state: '', postal_code: '',
                    guardian_name: '', guardian_address: '', guardian_phone: ''
                  })}
                  title="Clear Form"
                >
                  🗑️
                </button>
              </div>
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

                {/* Address Information Section */}
                <div className="form-section">
                  <h3 className="form-section-title">Address Information</h3>
                  
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="address_line1">Address Line 1 *</label>
                      <input
                        type="text"
                        id="address_line1"
                        value={newStudent.address_line1}
                        onChange={(e) => setNewStudent({
                          ...newStudent,
                          address_line1: e.target.value
                        })}
                        required
                        placeholder="Enter address line 1"
                      />
                    </div>
                    
                    <div className="form-group">
                      <label htmlFor="address_line2">Address Line 2</label>
                      <input
                        type="text"
                        id="address_line2"
                        value={newStudent.address_line2}
                        onChange={(e) => setNewStudent({
                          ...newStudent,
                          address_line2: e.target.value
                        })}
                        placeholder="Enter address line 2 (optional)"
                      />
                    </div>
                  </div>

                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="city">City *</label>
                      <input
                        type="text"
                        id="city"
                        value={newStudent.city}
                        onChange={(e) => setNewStudent({
                          ...newStudent,
                          city: e.target.value
                        })}
                        required
                        placeholder="Enter city"
                      />
                    </div>
                    
                    <div className="form-group">
                      <label htmlFor="state">State *</label>
                      <input
                        type="text"
                        id="state"
                        value={newStudent.state}
                        onChange={(e) => setNewStudent({
                          ...newStudent,
                          state: e.target.value
                        })}
                        required
                        placeholder="Enter state"
                      />
                    </div>
                  </div>

                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="postal_code">Postal Code *</label>
                      <input
                        type="text"
                        id="postal_code"
                        value={newStudent.postal_code}
                        onChange={(e) => {
                          const value = e.target.value.replace(/\D/g, '').slice(0, 6);
                          setNewStudent({
                            ...newStudent,
                            postal_code: value
                          });
                        }}
                        required
                        placeholder="Enter 6-digit postal code"
                        pattern="^[0-9]{6}$"
                        maxLength="6"
                        title="Please enter a valid 6-digit postal code"
                      />
                      {newStudent.postal_code && newStudent.postal_code.length > 0 && newStudent.postal_code.length < 6 && (
                        <span className="validation-error">Postal code must be 6 digits</span>
                      )}
                      {newStudent.postal_code && newStudent.postal_code.length === 6 && (
                        <span className="validation-success">✓ Valid postal code</span>
                      )}
                    </div>
                  </div>
                </div>

                {/* Guardian Information Section */}
                <div className="form-section">
                  <h3 className="form-section-title">Guardian Information</h3>
                  
                  <div className="form-row">
                    <div className="form-group">
                      <label htmlFor="guardian_name">Guardian Name *</label>
                      <input
                        type="text"
                        id="guardian_name"
                        value={newStudent.guardian_name}
                        onChange={(e) => setNewStudent({
                          ...newStudent,
                          guardian_name: e.target.value
                        })}
                        required
                        placeholder="Enter guardian's full name"
                      />
                    </div>
                    
                    <div className="form-group">
                      <label htmlFor="guardian_phone">Guardian Phone *</label>
                      <input
                        type="tel"
                        id="guardian_phone"
                        value={newStudent.guardian_phone}
                        onChange={(e) => {
                          const value = e.target.value.replace(/\D/g, '').slice(0, 10);
                          setNewStudent({
                            ...newStudent,
                            guardian_phone: value
                          });
                        }}
                        required
                        placeholder="Enter 10-digit mobile number"
                        pattern="^[6-9][0-9]{9}$"
                        maxLength="10"
                        title="Please enter a valid 10-digit Indian mobile number starting with 6, 7, 8, or 9"
                      />
                      {newStudent.guardian_phone && newStudent.guardian_phone.length > 0 && newStudent.guardian_phone.length < 10 && (
                        <span className="validation-error">Phone number must be 10 digits</span>
                      )}
                      {newStudent.guardian_phone && newStudent.guardian_phone.length === 10 && !/^[6-9]/.test(newStudent.guardian_phone) && (
                        <span className="validation-error">Phone number must start with 6, 7, 8, or 9</span>
                      )}
                      {newStudent.guardian_phone && newStudent.guardian_phone.length === 10 && /^[6-9][0-9]{9}$/.test(newStudent.guardian_phone) && (
                        <span className="validation-success">✓ Valid phone number</span>
                      )}
                    </div>
                  </div>

                  <div className="form-row">
                    <div className="form-group full-width">
                      <label htmlFor="guardian_address">Guardian Address *</label>
                      <textarea
                        id="guardian_address"
                        value={newStudent.guardian_address}
                        onChange={(e) => setNewStudent({
                          ...newStudent,
                          guardian_address: e.target.value
                        })}
                        required
                        placeholder="Enter guardian's complete address"
                        rows="3"
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
                      branch: '',
                      address_line1: '',
                      address_line2: '',
                      city: '',
                      state: '',
                      postal_code: '',
                      guardian_name: '',
                      guardian_address: '',
                      guardian_phone: ''
                    })}
                  >
                    Reset Form
                  </button>
                </div>
              </form>
            </div>
          )}

          {activeTab === 'students' && (
            <div className="students-list-section">
              <div className="section-header">
                <h2>Students List</h2>
                <button 
                  className="refresh-btn"
                  onClick={() => fetchStudentsData()}
                  title="Refresh Students Data"
                >
                  🔄
                </button>
              </div>
              
              <div className="students-summary">
                <p>Total Students: <strong>{students.length}</strong></p>
              </div>

              {students.length === 0 ? (
                <div className="no-students">
                  <p>No students found. Create a student account first.</p>
                </div>
              ) : (
                <div className="students-table-container">
                  <table className="students-table">
                    <thead>
                      <tr>
                        <th>Name</th>
                        <th>Roll No</th>
                        <th>Phone</th>
                        <th>Date of Birth</th>
                        <th>Stream</th>
                        <th>Room</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {students.map(student => (
                        <tr key={student.id}>
                          <td className="student-name">{student.full_name}</td>
                          <td className="roll-no">{student.roll_no}</td>
                          <td className="phone">{student.phone}</td>
                          <td className="dob">
                            {student.date_of_birth ? new Date(student.date_of_birth).toLocaleDateString() : 'N/A'}
                          </td>
                          <td className="stream">{student.stream}</td>
                          <td className="room">
                            {student.room_number ? `Room ${student.room_number}` : 'Not Assigned'}
                          </td>
                                                     <td className="actions">
                             <button
                               className="action-btn view-btn"
                               onClick={() => handleViewStudent(student)}
                               title="View Details"
                             >
                               👁️
                             </button>
                             <button
                               className="action-btn edit-btn"
                               onClick={() => handleEditStudent(student)}
                               title="Edit Student"
                             >
                               ✏️
                             </button>
                             <button
                               className="action-btn delete-btn"
                               onClick={() => handleDeleteConfirm(student)}
                               title="Delete Student"
                             >
                               🗑️
                             </button>
                           </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          )}

          {activeTab === 'assign' && (
            <div className="assign-section">
              <div className="section-header">
                <h2>Assign Room to Student</h2>
                <button 
                  className="refresh-btn"
                  onClick={() => fetchData()}
                  title="Refresh Room Data"
                >
                  🔄
                </button>
              </div>
              <form onSubmit={handleRoomAssignment} className="assign-form">
                <div className="form-group">
                  <label htmlFor="student_id">Student Roll Number</label>
                  <input
                    type="text"
                    id="student_id"
                    value={roomAssignment.studentId}
                    onChange={(e) => setRoomAssignment({
                      ...roomAssignment,
                      studentId: e.target.value
                    })}
                    required
                    placeholder="Enter student roll number (e.g., CS2021001)"
                  />
                  <small className="field-note">Enter the student's roll number</small>
                </div>

                <div className="form-group">
                  <label htmlFor="room_select">Select Room</label>
                  <select
                    id="room_select"
                    value={roomAssignment.roomId}
                    onChange={(e) => handleRoomSelection(e.target.value)}
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

                {roomAssignment.roomId && (
                  <div className="form-group">
                    <label htmlFor="bed_select">Select Bed</label>
                    <select
                      id="bed_select"
                      value={roomAssignment.bedNumber}
                      onChange={(e) => setRoomAssignment({
                        ...roomAssignment,
                        bedNumber: e.target.value
                      })}
                      required
                      disabled={bedsLoading}
                    >
                      <option value="">
                        {bedsLoading ? "Loading beds..." : "Choose a bed..."}
                      </option>
                      {!bedsLoading && availableBeds.length > 0 ? (
                        availableBeds.map(bedNumber => (
                          <option key={bedNumber} value={bedNumber}>
                            Bed {bedNumber}
                          </option>
                        ))
                      ) : !bedsLoading && availableBeds.length === 0 ? (
                        <option value="" disabled>No available beds</option>
                      ) : null}
                    </select>
                    {!bedsLoading && availableBeds.length === 0 && roomAssignment.roomId && (
                      <small className="field-note error">No available beds in this room</small>
                    )}
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
              <div className="section-header">
                <h2>Room Change Requests</h2>
                <button 
                  className="refresh-btn"
                  onClick={() => fetchData()}
                  title="Refresh Requests Data"
                >
                  🔄
                </button>
              </div>
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
                        <p><strong>Requested Room:</strong> {request.requested_room} (Bed {request.requested_bed_number})</p>
                        <p><strong>Reason:</strong> {request.reason}</p>
                        <p><strong>Requested Date:</strong> {new Date(request.requested_at).toLocaleDateString()}</p>
                      </div>
                      {request.status === 'pending' && (
                        <div className="request-actions">
                          <button 
                            className="approve-button"
                            onClick={() => handleRequestAction(request, 'approve')}
                            disabled={processingRequest === request.id}
                          >
                            {processingRequest === request.id ? 'Processing...' : 'Approve'}
                          </button>
                          <button 
                            className="reject-button"
                            onClick={() => handleRequestAction(request, 'reject')}
                            disabled={processingRequest === request.id}
                          >
                            {processingRequest === request.id ? 'Processing...' : 'Reject'}
                          </button>
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

      {/* Confirmation Modal for Room Change Requests */}
      {showConfirmModal && (
        <div className="modal-overlay">
          <div className="modal-content confirmation-modal">
            <h3>Confirm Action</h3>
            <p>
              Are you sure you want to <strong>{confirmAction.type}</strong> the room change request from{' '}
              <strong>{confirmAction.request?.student_name}</strong>?
            </p>
            
            {confirmAction.type === 'approve' && (
              <div className="action-details">
                <p><strong>Student:</strong> {confirmAction.request?.student_name}</p>
                <p><strong>From:</strong> {confirmAction.request?.current_room || 'Not assigned'}</p>
                <p><strong>To:</strong> {confirmAction.request?.requested_room} (Bed {confirmAction.request?.requested_bed_number})</p>
                <p><strong>Reason:</strong> {confirmAction.request?.reason}</p>
              </div>
            )}
            
            <div className="modal-actions">
              <button 
                className="cancel-button" 
                onClick={cancelRequestAction}
              >
                Cancel
              </button>
              <button 
                className={`confirm-button ${confirmAction.type === 'approve' ? 'approve' : 'reject'}`}
                onClick={confirmRequestAction}
              >
                {confirmAction.type === 'approve' ? 'Approve Request' : 'Reject Request'}
              </button>
            </div>
          </div>
        </div>
      )}

             {/* View Student Details Modal */}
       {showStudentModal && selectedStudent && (
         <div className="modal-overlay">
           <div className="modal-content view-student-modal">
             <div className="modal-header">
               <h3>Student Details</h3>
               <button 
                 className="close-btn"
                 onClick={handleCloseStudentModal}
                 title="Close"
               >
                 ✖️
               </button>
             </div>
             
             <div className="student-details-container">
               {/* Personal Information Section */}
               <div className="details-section">
                 <h4 className="section-title">📋 Personal Information</h4>
                 <div className="details-grid">
                   <div className="detail-item">
                     <span className="label">Full Name:</span>
                     <span className="value">{selectedStudent.full_name}</span>
                   </div>
                   <div className="detail-item">
                     <span className="label">Date of Birth:</span>
                     <span className="value">
                       {selectedStudent.date_of_birth 
                         ? new Date(selectedStudent.date_of_birth).toLocaleDateString() 
                         : 'N/A'}
                     </span>
                   </div>
                   <div className="detail-item">
                     <span className="label">Gender:</span>
                     <span className="value">{selectedStudent.gender || 'N/A'}</span>
                   </div>
                   <div className="detail-item">
                     <span className="label">Aadhaar ID:</span>
                     <span className="value">{selectedStudent.aadhaar_id || 'N/A'}</span>
                   </div>
                 </div>
               </div>

               {/* Contact Information Section */}
               <div className="details-section">
                 <h4 className="section-title">📞 Contact Information</h4>
                 <div className="details-grid">
                   <div className="detail-item">
                     <span className="label">Email:</span>
                     <span className="value">{selectedStudent.email || 'N/A'}</span>
                   </div>
                   <div className="detail-item">
                     <span className="label">Phone:</span>
                     <span className="value">{selectedStudent.phone}</span>
                   </div>
                 </div>
               </div>

               {/* Address Information Section */}
               <div className="details-section">
                 <h4 className="section-title">🏠 Address Information</h4>
                 <div className="details-grid">
                   <div className="detail-item">
                     <span className="label">Address Line 1:</span>
                     <span className="value">{selectedStudent.address_line1 || 'N/A'}</span>
                   </div>
                   <div className="detail-item">
                     <span className="label">Address Line 2:</span>
                     <span className="value">{selectedStudent.address_line2 || 'N/A'}</span>
                   </div>
                   <div className="detail-item">
                     <span className="label">City:</span>
                     <span className="value">{selectedStudent.city || 'N/A'}</span>
                   </div>
                   <div className="detail-item">
                     <span className="label">State:</span>
                     <span className="value">{selectedStudent.state || 'N/A'}</span>
                   </div>
                   <div className="detail-item">
                     <span className="label">Postal Code:</span>
                     <span className="value">{selectedStudent.postal_code || 'N/A'}</span>
                   </div>
                 </div>
               </div>

               {/* Guardian Information Section */}
               <div className="details-section">
                 <h4 className="section-title">👨‍👩‍👦 Guardian Information</h4>
                 <div className="details-grid">
                   <div className="detail-item">
                     <span className="label">Guardian Name:</span>
                     <span className="value">{selectedStudent.guardian_name || 'N/A'}</span>
                   </div>
                   <div className="detail-item">
                     <span className="label">Guardian Phone:</span>
                     <span className="value">{selectedStudent.guardian_phone || 'N/A'}</span>
                   </div>
                   <div className="detail-item full-width">
                     <span className="label">Guardian Address:</span>
                     <span className="value">{selectedStudent.guardian_address || 'N/A'}</span>
                   </div>
                 </div>
               </div>

               {/* Academic Information Section */}
               <div className="details-section">
                 <h4 className="section-title">🎓 Academic Information</h4>
                 <div className="details-grid">
                   <div className="detail-item">
                     <span className="label">Roll Number:</span>
                     <span className="value roll-highlight">{selectedStudent.roll_no}</span>
                   </div>
                   <div className="detail-item">
                     <span className="label">Username:</span>
                     <span className="value">{selectedStudent.username}</span>
                   </div>
                   <div className="detail-item">
                     <span className="label">Stream:</span>
                     <span className="value">{selectedStudent.stream}</span>
                   </div>
                   <div className="detail-item">
                     <span className="label">Branch:</span>
                     <span className="value">{selectedStudent.branch}</span>
                   </div>
                 </div>
               </div>

               {/* Room Assignment Section */}
               <div className="details-section">
                 <h4 className="section-title">🏠 Room Assignment</h4>
                 <div className="details-grid">
                   <div className="detail-item">
                     <span className="label">Room:</span>
                     <span className={`value ${selectedStudent.room_number ? 'room-assigned' : 'room-unassigned'}`}>
                       {selectedStudent.room_number ? `Room ${selectedStudent.room_number}` : 'Not Assigned'}
                     </span>
                   </div>
                   {selectedStudent.bed_number && (
                     <div className="detail-item">
                       <span className="label">Bed Number:</span>
                       <span className="value">Bed {selectedStudent.bed_number}</span>
                     </div>
                   )}
                 </div>
               </div>

               {/* Account Information Section */}
               <div className="details-section">
                 <h4 className="section-title">⚙️ Account Information</h4>
                 <div className="details-grid">
                   <div className="detail-item">
                     <span className="label">Account Created:</span>
                     <span className="value">
                       {selectedStudent.created_at 
                         ? new Date(selectedStudent.created_at).toLocaleString() 
                         : 'N/A'}
                     </span>
                   </div>
                   <div className="detail-item">
                     <span className="label">First Login Status:</span>
                     <span className={`value ${selectedStudent.first_login ? 'first-login-pending' : 'first-login-completed'}`}>
                       {selectedStudent.first_login ? 'Password Change Required' : 'Completed'}
                     </span>
                   </div>
                 </div>
               </div>
             </div>

             <div className="modal-actions">
               <button 
                 className="confirm-button approve"
                 onClick={handleCloseStudentModal}
               >
                 Close
               </button>
             </div>
           </div>
         </div>
       )}

       {/* Edit Student Modal */}
       {showEditModal && editingStudent && (
         <div className="modal-overlay">
           <div className="modal-content edit-student-modal">
             <h3>Edit Student</h3>
            <form onSubmit={handleSaveEdit} className="edit-student-form">
              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="edit_full_name">Full Name</label>
                  <input
                    type="text"
                    id="edit_full_name"
                    value={editingStudent.full_name}
                    onChange={(e) => setEditingStudent({
                      ...editingStudent,
                      full_name: e.target.value
                    })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="edit_email">Email</label>
                  <input
                    type="email"
                    id="edit_email"
                    value={editingStudent.email}
                    onChange={(e) => setEditingStudent({
                      ...editingStudent,
                      email: e.target.value
                    })}
                  />
                </div>
              </div>

              <div className="form-row">
                <div className="form-group">
                  <label htmlFor="edit_phone">Phone</label>
                  <input
                    type="tel"
                    id="edit_phone"
                    value={editingStudent.phone}
                    onChange={(e) => setEditingStudent({
                      ...editingStudent,
                      phone: e.target.value.replace(/\D/g, '').slice(0, 10)
                    })}
                    pattern="^[6-9][0-9]{9}$"
                    maxLength="10"
                    required
                  />
                </div>
                <div className="form-group">
                  <label htmlFor="edit_stream">Stream</label>
                  <select
                    id="edit_stream"
                    value={editingStudent.stream}
                    onChange={(e) => setEditingStudent({
                      ...editingStudent,
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
                  <label htmlFor="edit_branch">Branch</label>
                  <input
                    type="text"
                    id="edit_branch"
                    value={editingStudent.branch}
                    onChange={(e) => setEditingStudent({
                      ...editingStudent,
                      branch: e.target.value
                    })}
                    required
                  />
                </div>
              </div>

              <div className="modal-actions">
                <button type="button" className="cancel-button" onClick={handleCancelEdit}>
                  Cancel
                </button>
                <button type="submit" className="confirm-button approve" disabled={studentsLoading}>
                  {studentsLoading ? 'Saving...' : 'Save Changes'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Delete Confirmation Modal */}
      {deleteConfirm && (
        <div className="modal-overlay">
          <div className="modal-content confirmation-modal">
            <h3>Confirm Delete</h3>
            <p>
              Are you sure you want to delete student <strong>{deleteConfirm.full_name}</strong>?
            </p>
            <div className="action-details">
              <p><strong>Roll No:</strong> {deleteConfirm.roll_no}</p>
              <p><strong>Stream:</strong> {deleteConfirm.stream}</p>
              <p><strong>Branch:</strong> {deleteConfirm.branch}</p>
              <p className="warning">⚠️ This action cannot be undone. The student will be removed from any assigned room.</p>
            </div>
            
            <div className="modal-actions">
              <button className="cancel-button" onClick={handleCancelDelete}>
                Cancel
              </button>
              <button 
                className="confirm-button reject" 
                onClick={handleDeleteStudent}
                disabled={studentsLoading}
              >
                {studentsLoading ? 'Deleting...' : 'Delete Student'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default WardenDashboard; 