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
  const [personalDetailsRequests, setPersonalDetailsRequests] = useState([]);
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
    password: '',
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
  
  // Student Management states (EDIT FUNCTIONALITY REMOVED)
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [showStudentModal, setShowStudentModal] = useState(false);
  const [deleteConfirm, setDeleteConfirm] = useState(null);
  const [passwordReset, setPasswordReset] = useState(null);
  const [studentsLoading, setStudentsLoading] = useState(false);

  // Tab-specific refresh loading states
  const [overviewRefreshing, setOverviewRefreshing] = useState(false);
  const [studentsRefreshing, setStudentsRefreshing] = useState(false);
  const [roomsRefreshing, setRoomsRefreshing] = useState(false);
  const [requestsRefreshing, setRequestsRefreshing] = useState(false);

  // Personal details requests modal state
  const [showCommentsModal, setShowCommentsModal] = useState(false);
  const [selectedRequest, setSelectedRequest] = useState(null);
  const [actionType, setActionType] = useState('');
  const [wardenComments, setWardenComments] = useState('');

  // Pagination states
  const [currentPage, setCurrentPage] = useState(1);
  const [studentsPerPage] = useState(5);

  // Add Student form loading state
  const [addingStudent, setAddingStudent] = useState(false);

  // Room change request states
  const [processingRequest, setProcessingRequest] = useState(null);
  const [showConfirmModal, setShowConfirmModal] = useState(false);
  const [confirmAction, setConfirmAction] = useState({ type: '', request: null });

  const [selectedRoom, setSelectedRoom] = useState(null);
  const [selectedRoomDetails, setSelectedRoomDetails] = useState(null);
  const [showBedLayout, setShowBedLayout] = useState(false);
  const [allStudents, setAllStudents] = useState([]);

  useEffect(() => {
    fetchData();
  }, []);

  // Debug log for student count
  useEffect(() => {
    console.log('Current students count for overview:', students.length);
    console.log('Students data:', students);
  }, [students]);

  // Additional useEffect to fetch students when students tab is activated (only if not already loaded)
  useEffect(() => {
    if (activeTab === 'students' && students.length === 0) {
      console.log('Students tab activated, fetching students...');
      fetchStudentsData();
    }
  }, [activeTab]);

  // Fetch all students when rooms tab is activated (only if not already loaded)
  useEffect(() => {
    if (activeTab === 'rooms' && allStudents.length === 0) {
      console.log('Rooms tab activated, fetching all students for assignment...');
      fetchAllStudentsForAssignment();
    }
  }, [activeTab]);

  // Pagination logic
  const totalPages = Math.ceil(students.length / studentsPerPage);
  const indexOfLastStudent = currentPage * studentsPerPage;
  const indexOfFirstStudent = indexOfLastStudent - studentsPerPage;
  const currentStudents = students.slice(indexOfFirstStudent, indexOfLastStudent);

  const paginate = (pageNumber) => {
    setCurrentPage(pageNumber);
  };

  const nextPage = () => {
    if (currentPage < totalPages) {
      setCurrentPage(currentPage + 1);
    }
  };

  const prevPage = () => {
    if (currentPage > 1) {
      setCurrentPage(currentPage - 1);
    }
  };

  // Reset to first page when students data changes
  useEffect(() => {
    setCurrentPage(1);
  }, [students.length]);

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

  const fetchAllStudentsForAssignment = async () => {
    console.log('Fetching all students for assignment...');
    const studentsResult = await apiCall('GET', '/api/warden/students');
    
    if (studentsResult.success) {
      setAllStudents(studentsResult.data);
      console.log('All students loaded for assignment:', studentsResult.data);
    } else {
      console.error('Failed to fetch all students:', studentsResult.error);
      setMessage(`Error fetching students: ${studentsResult.error}`);
    }
  };
  
  const fetchData = async () => {
    setLoading(true);
    console.log('Fetching all dashboard data...');
    
    // Fetch rooms
    const roomsResult = await apiCall('GET', '/api/rooms');
    console.log('Rooms API response:', roomsResult);
    if (roomsResult.success) {
      setRooms(roomsResult.data);
    } else {
      console.error('Failed to fetch rooms:', roomsResult.error);
    }

    // Fetch room change requests
    const requestsResult = await apiCall('GET', '/api/warden/room-change-requests');
    console.log('Requests API response:', requestsResult);
    if (requestsResult.success) {
      setRoomChangeRequests(requestsResult.data);
    } else {
      console.error('Failed to fetch room change requests:', requestsResult.error);
    }

    // Fetch personal details update requests
    const personalDetailsResult = await apiCall('GET', '/api/warden/personal-details-update-requests');
    console.log('Personal Details Requests API response:', personalDetailsResult);
    if (personalDetailsResult.success) {
      setPersonalDetailsRequests(personalDetailsResult.data);
    } else {
      console.error('Failed to fetch personal details update requests:', personalDetailsResult.error);
    }

    // Fetch all students for overview display and general use
    const studentsResult = await apiCall('GET', '/api/warden/students');
    console.log('Students API response:', studentsResult);
    if (studentsResult.success) {
      setStudents(studentsResult.data);
      setAllStudents(studentsResult.data); // Set both states for consistent data
      console.log('✅ Students data loaded on page load for overview display');
      console.log(`📊 Total students loaded: ${studentsResult.data.length}`);
      console.log('Students data:', studentsResult.data);
    } else {
      console.error('❌ Failed to fetch students:', studentsResult.error);
      setMessage(`Error fetching students: ${studentsResult.error}`);
    }
    
    setLoading(false);
  };

  // Student management functions (EDIT REMOVED)
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

  const handleCancelDelete = () => {
    setDeleteConfirm(null);
  };

  // Room assignment functions
  const handleRoomChange = async (e) => {
    const selectedRoomId = e.target.value;
    setRoomAssignment({
      ...roomAssignment,
      roomId: selectedRoomId,
      bedNumber: ''
    });

    if (selectedRoomId) {
      setBedsLoading(true);
      const beds = await getAvailableBeds(selectedRoomId);
      setAvailableBeds(beds);
      setBedsLoading(false);
    } else {
      setAvailableBeds([]);
    }
  };

  const getAvailableBeds = async (roomId) => {
    if (!roomId) return [];
    
    try {
      const result = await apiCall('GET', `/api/rooms/${roomId}`);
      
      if (result.success && result.data && result.data.beds) {
        const availableBeds = result.data.beds
          .filter(bed => bed.status === 'available')
          .map(bed => bed.bed_number)
          .sort((a, b) => a - b);
        
        return availableBeds;
      }
      return [];
    } catch (error) {
      console.error('Error fetching beds:', error);
      return [];
    }
  };

  const handleRoomAssignment = async (e) => {
    e.preventDefault();
    
    setStudentsLoading(true);
    
    try {
      const assignmentData = {
        studentId: roomAssignment.studentId, // This is actually roll number
        roomId: roomAssignment.roomId,
        bedNumber: parseInt(roomAssignment.bedNumber)
      };

      const result = await apiCall('POST', '/api/warden/assign-room', assignmentData);
      
      if (result.success) {
        setMessage('Room assigned successfully!');
        setRoomAssignment({ studentId: '', roomId: '', bedNumber: '' });
        setAvailableBeds([]);
        // Refresh data
        await fetchData();
        await fetchAllStudentsForAssignment();
      } else {
        setMessage(`Error: ${result.error}`);
      }
    } catch (error) {
      setMessage(`Error: ${error.message}`);
    }
    
    setStudentsLoading(false);
    setTimeout(() => setMessage(''), 5000);
  };

  const handleRequestAction = async (request, action) => {
    try {
      const result = await apiCall('PUT', `/api/warden/room-change-requests/${request.id}/${action}`, {});
      
      if (result.success) {
        setMessage(`Request ${action}d successfully!`);
        // Refresh room change requests
        await fetchData();
      } else {
        setMessage(`Error: ${result.error}`);
      }
    } catch (error) {
      setMessage(`Error: ${error.message}`);
    }
    
    setTimeout(() => setMessage(''), 5000);
  };

  const handlePersonalDetailsAction = (request, action) => {
    setSelectedRequest(request);
    setActionType(action);
    setWardenComments('');
    setShowCommentsModal(true);
  };

  const submitPersonalDetailsAction = async () => {
    try {
      const result = await apiCall('PUT', `/api/warden/personal-details-update-requests/${selectedRequest.id}/${actionType}`, {
        comments: wardenComments
      });
      
      if (result.success) {
        setMessage(`Personal details request ${actionType}d successfully!`);
        setShowCommentsModal(false);
        setSelectedRequest(null);
        setActionType('');
        setWardenComments('');
        // Refresh data
        await fetchData();
      } else {
        setMessage(`Error: ${result.error}`);
      }
    } catch (error) {
      setMessage(`Error: ${error.message}`);
    }
    
    setTimeout(() => setMessage(''), 5000);
  };

  // Tab-specific refresh functions
  const refreshOverview = async () => {
    setOverviewRefreshing(true);
    console.log('🔄 Refreshing overview data...');
    
    try {
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

      // Fetch students
      const studentsResult = await apiCall('GET', '/api/warden/students');
      if (studentsResult.success) {
        setStudents(studentsResult.data);
        setAllStudents(studentsResult.data);
      }

      console.log('✅ Overview data refreshed successfully');
    } catch (error) {
      console.error('❌ Error refreshing overview:', error);
      setMessage('Error refreshing overview data');
      setTimeout(() => setMessage(''), 5000);
    } finally {
      setOverviewRefreshing(false);
    }
  };

  const refreshStudents = async () => {
    setStudentsRefreshing(true);
    console.log('🔄 Refreshing students data...');
    
    try {
      const studentsResult = await apiCall('GET', '/api/warden/students');
      if (studentsResult.success) {
        setStudents(studentsResult.data);
        setAllStudents(studentsResult.data);
        console.log('✅ Students data refreshed successfully');
      } else {
        setMessage('Error refreshing students data');
        setTimeout(() => setMessage(''), 5000);
      }
    } catch (error) {
      console.error('❌ Error refreshing students:', error);
      setMessage('Error refreshing students data');
      setTimeout(() => setMessage(''), 5000);
    } finally {
      setStudentsRefreshing(false);
    }
  };

  const refreshRooms = async () => {
    setRoomsRefreshing(true);
    console.log('🔄 Refreshing rooms data...');
    
    try {
      // Fetch rooms
      const roomsResult = await apiCall('GET', '/api/rooms');
      if (roomsResult.success) {
        setRooms(roomsResult.data);
      }

      // Fetch students for room assignment
      const studentsResult = await apiCall('GET', '/api/warden/students');
      if (studentsResult.success) {
        setAllStudents(studentsResult.data);
      }

      console.log('✅ Rooms data refreshed successfully');
    } catch (error) {
      console.error('❌ Error refreshing rooms:', error);
      setMessage('Error refreshing rooms data');
      setTimeout(() => setMessage(''), 5000);
    } finally {
      setRoomsRefreshing(false);
    }
  };

  const refreshRequests = async () => {
    setRequestsRefreshing(true);
    console.log('🔄 Refreshing requests data...');
    
    try {
      // Fetch room change requests
      const requestsResult = await apiCall('GET', '/api/warden/room-change-requests');
      if (requestsResult.success) {
        setRoomChangeRequests(requestsResult.data);
        console.log('✅ Room change requests refreshed successfully');
      } else {
        setMessage('Error refreshing room change requests');
        setTimeout(() => setMessage(''), 5000);
      }

      // Fetch personal details update requests
      const personalDetailsResult = await apiCall('GET', '/api/warden/personal-details-update-requests');
      if (personalDetailsResult.success) {
        setPersonalDetailsRequests(personalDetailsResult.data);
        console.log('✅ Personal details requests refreshed successfully');
      } else {
        setMessage('Error refreshing personal details requests');
        setTimeout(() => setMessage(''), 5000);
      }
    } catch (error) {
      console.error('❌ Error refreshing requests:', error);
      setMessage('Error refreshing requests data');
      setTimeout(() => setMessage(''), 5000);
    } finally {
      setRequestsRefreshing(false);
    }
  };

  // Add Student functions
  const handleStudentInputChange = (e) => {
    const { name, value } = e.target;
    setNewStudent(prevState => ({
      ...prevState,
      [name]: value
    }));
  };

  const handleAddStudent = async (e) => {
    e.preventDefault();
    setAddingStudent(true);
    
    try {
      // Validate required fields based on backend requirements
      const requiredFields = {
        full_name: 'Full Name',
        email: 'Email',
        phone: 'Phone',
        date_of_birth: 'Date of Birth',
        gender: 'Gender',
        aadhaar_id: 'Aadhaar ID',
        roll_no: 'Roll Number',
        stream: 'Stream',
        branch: 'Branch',
        address_line1: 'Address Line 1',
        city: 'City',
        state: 'State',
        postal_code: 'Postal Code',
        guardian_name: 'Guardian Name',
        guardian_address: 'Guardian Address',
        guardian_phone: 'Guardian Phone'
      };

      const missingFields = [];
      for (const [field, label] of Object.entries(requiredFields)) {
        if (!newStudent[field] || newStudent[field].trim() === '') {
          missingFields.push(label);
        }
      }

      if (missingFields.length > 0) {
        setMessage(`Error: Please fill in all required fields: ${missingFields.join(', ')}`);
        setTimeout(() => setMessage(''), 5000);
        return;
      }

      // Prepare data for backend (remove password field as backend generates it)
      const { password, ...studentData } = newStudent;
      console.log('Adding new student:', studentData);
      const result = await apiCall('POST', '/api/warden/create-student', studentData);
      
      if (result.success) {
        // Show success message with generated credentials
        const credentials = result.data?.credentials;
        let successMessage = 'Student added successfully!';
        if (credentials) {
          successMessage += ` Login credentials - Username: ${credentials.username}, Password: ${credentials.password}`;
        }
        setMessage(successMessage);
        
        // Reset form
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
          password: '',
          address_line1: '',
          address_line2: '',
          city: '',
          state: '',
          postal_code: '',
          guardian_name: '',
          guardian_address: '',
          guardian_phone: ''
        });
        // Refresh students data
        await refreshStudents();
        setTimeout(() => setMessage(''), 10000); // Longer timeout for credentials
      } else {
        setMessage(`Error: ${result.error}`);
        setTimeout(() => setMessage(''), 5000);
      }
    } catch (error) {
      console.error('Error adding student:', error);
      setMessage('Error adding student');
      setTimeout(() => setMessage(''), 5000);
    } finally {
      setAddingStudent(false);
    }
  };
  
  if (loading) {
    return <div className="loading">Loading dashboard...</div>;
  }

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="header-content">
          <h1>Warden Portal</h1>
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
            Students
          </button>
          <button 
            className={`nav-button ${activeTab === 'add-student' ? 'active' : ''}`}
            onClick={() => setActiveTab('add-student')}
          >
            Add Student
          </button>
          <button 
            className={`nav-button ${activeTab === 'rooms' ? 'active' : ''}`}
            onClick={() => setActiveTab('rooms')}
          >
            Rooms
          </button>
          <button 
            className={`nav-button ${activeTab === 'requests' ? 'active' : ''}`}
            onClick={() => setActiveTab('requests')}
          >
            Room Requests
          </button>
        </nav>

        <main className="dashboard-main">
          {message && <div className="message">{message}</div>}
          

          
          {activeTab === 'students' && (
            <div className="students-section">
              {/* Tab Header with Refresh */}
              <div className="tab-header">
                <h2>Student Management</h2>
                <button 
                  className={`refresh-btn ${studentsRefreshing ? 'refreshing' : ''}`}
                  onClick={refreshStudents}
                  disabled={studentsRefreshing}
                  title="Refresh Students Data"
                >
                  {studentsRefreshing ? '🔄' : '↻'}
                </button>
              </div>
              
              <div className={`students-table-container ${studentsRefreshing ? 'loading' : ''}`}>
                <h3>All Students</h3>
                <table className="students-table">
                  <thead>
                    <tr>
                      <th>Name</th>
                      <th>Roll No</th>
                      <th>Stream</th>
                      <th>Branch</th>
                      <th>Room</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {currentStudents.length > 0 ? (
                      currentStudents.map(student => (
                        <tr key={student.id}>
                          <td>{student.full_name}</td>
                          <td>{student.roll_no}</td>
                          <td>{student.stream}</td>
                          <td>{student.branch}</td>
                          <td>
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
                              className="action-btn delete-btn"
                              onClick={() => handleDeleteConfirm(student)}
                              title="Delete Student"
                            >
                              🗑️
                            </button>
                          </td>
                        </tr>
                      ))
                    ) : (
                      <tr>
                        <td colSpan="6" className="no-students-cell">
                          <div className="no-students-message">
                            <div className="empty-icon">👥</div>
                            <div>No students found</div>
                          </div>
                        </td>
                      </tr>
                    )}
                  </tbody>
                </table>

                {/* Pagination Controls */}
                {students.length > 0 && (
                  <div className="pagination-container">
                    <div className="pagination-info">
                      <span>
                        Showing {indexOfFirstStudent + 1} to {Math.min(indexOfLastStudent, students.length)} of {students.length} students
                      </span>
                    </div>
                    <div className="pagination-controls">
                      <button 
                        className={`pagination-btn prev-btn ${currentPage === 1 ? 'disabled' : ''}`}
                        onClick={prevPage}
                        disabled={currentPage === 1}
                      >
                        ← Previous
                      </button>
                      
                      <div className="page-numbers">
                        {Array.from({ length: totalPages }, (_, index) => (
                          <button
                            key={index + 1}
                            className={`page-btn ${currentPage === index + 1 ? 'active' : ''}`}
                            onClick={() => paginate(index + 1)}
                          >
                            {index + 1}
                          </button>
                        ))}
                      </div>
                      
                      <button 
                        className={`pagination-btn next-btn ${currentPage === totalPages ? 'disabled' : ''}`}
                        onClick={nextPage}
                        disabled={currentPage === totalPages}
                      >
                        Next →
                      </button>
                    </div>
                  </div>
                )}
              </div>
            </div>
          )}

          {activeTab === 'add-student' && (
            <div className="add-student-section">
              {/* Tab Header with Refresh */}
              <div className="tab-header">
                <h2>Add New Student</h2>
              </div>
              
              <div className="add-student-container">
                <form onSubmit={handleAddStudent} className="add-student-form">
                  {/* Personal Information Section */}
                  <div className="form-section">
                    <h3 className="section-title">
                      <span className="section-icon">👤</span>
                      Personal Information
                    </h3>
                    
                    <div className="form-grid">
                      <div className="form-group">
                        <label htmlFor="full_name">Full Name *</label>
                        <input
                          type="text"
                          id="full_name"
                          name="full_name"
                          value={newStudent.full_name}
                          onChange={handleStudentInputChange}
                          required
                          placeholder="Enter full name"
                        />
                      </div>
                      
                      <div className="form-group">
                        <label htmlFor="email">Email *</label>
                        <input
                          type="email"
                          id="email"
                          name="email"
                          value={newStudent.email}
                          onChange={handleStudentInputChange}
                          required
                          placeholder="Enter email address"
                        />
                      </div>
                      
                                             <div className="form-group">
                         <label htmlFor="phone">Phone Number *</label>
                         <input
                           type="tel"
                           id="phone"
                           name="phone"
                           value={newStudent.phone}
                           onChange={handleStudentInputChange}
                           required
                           placeholder="Enter 10-digit phone number"
                         />
                       </div>
                       
                       <div className="form-group">
                         <label htmlFor="date_of_birth">Date of Birth *</label>
                         <input
                           type="date"
                           id="date_of_birth"
                           name="date_of_birth"
                           value={newStudent.date_of_birth}
                           onChange={handleStudentInputChange}
                           required
                         />
                       </div>
                       
                       <div className="form-group">
                         <label htmlFor="gender">Gender *</label>
                         <select
                           id="gender"
                           name="gender"
                           value={newStudent.gender}
                           onChange={handleStudentInputChange}
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
                           name="aadhaar_id"
                           value={newStudent.aadhaar_id}
                           onChange={handleStudentInputChange}
                           required
                           placeholder="Enter 12-digit Aadhaar number"
                           maxLength="12"
                           pattern="[0-9]{12}"
                         />
                       </div>
                    </div>
                  </div>

                  {/* Academic Information Section */}
                  <div className="form-section">
                    <h3 className="section-title">
                      <span className="section-icon">🎓</span>
                      Academic Information
                    </h3>
                    
                    <div className="form-grid">
                      <div className="form-group">
                        <label htmlFor="roll_no">Roll Number *</label>
                        <input
                          type="text"
                          id="roll_no"
                          name="roll_no"
                          value={newStudent.roll_no}
                          onChange={handleStudentInputChange}
                          required
                          placeholder="Enter roll number"
                        />
                      </div>
                      
                                             <div className="form-group">
                         <label htmlFor="stream">Stream *</label>
                         <input
                           type="text"
                           id="stream"
                           name="stream"
                           value={newStudent.stream}
                           onChange={handleStudentInputChange}
                           required
                           placeholder="e.g., Science, Commerce, Arts"
                         />
                       </div>
                       
                       <div className="form-group">
                         <label htmlFor="branch">Branch *</label>
                         <input
                           type="text"
                           id="branch"
                           name="branch"
                           value={newStudent.branch}
                           onChange={handleStudentInputChange}
                           required
                           placeholder="e.g., Computer Science, Biology"
                         />
                       </div>
                       
                       <div className="info-note">
                         <span className="note-icon">ℹ️</span>
                         <span>Login credentials will be automatically generated and displayed after student creation.</span>
                       </div>
                    </div>
                  </div>

                                     {/* Address Information Section */}
                   <div className="form-section">
                     <h3 className="section-title">
                       <span className="section-icon">🏠</span>
                       Address Information
                     </h3>
                     
                     <div className="address-grid">
                       {/* Address Lines Row */}
                       <div className="address-row">
                         <div className="form-group">
                           <label htmlFor="address_line1">Address Line 1 *</label>
                           <input
                             type="text"
                             id="address_line1"
                             name="address_line1"
                             value={newStudent.address_line1}
                             onChange={handleStudentInputChange}
                             required
                             placeholder="Street address, apartment, etc."
                           />
                         </div>
                         
                         <div className="form-group">
                           <label htmlFor="address_line2">Address Line 2</label>
                           <input
                             type="text"
                             id="address_line2"
                             name="address_line2"
                             value={newStudent.address_line2}
                             onChange={handleStudentInputChange}
                             placeholder="Additional address information (optional)"
                           />
                         </div>
                       </div>
                       
                       {/* City, State, Postal Code Row */}
                       <div className="location-row">
                         <div className="form-group">
                           <label htmlFor="city">City *</label>
                           <input
                             type="text"
                             id="city"
                             name="city"
                             value={newStudent.city}
                             onChange={handleStudentInputChange}
                             required
                             placeholder="Enter city"
                           />
                         </div>
                         
                         <div className="form-group">
                           <label htmlFor="state">State *</label>
                           <input
                             type="text"
                             id="state"
                             name="state"
                             value={newStudent.state}
                             onChange={handleStudentInputChange}
                             required
                             placeholder="Enter state"
                           />
                         </div>
                         
                         <div className="form-group">
                           <label htmlFor="postal_code">Postal Code *</label>
                           <input
                             type="text"
                             id="postal_code"
                             name="postal_code"
                             value={newStudent.postal_code}
                             onChange={handleStudentInputChange}
                             required
                             placeholder="Enter 6-digit postal code"
                             maxLength="6"
                             pattern="[0-9]{6}"
                           />
                         </div>
                       </div>
                     </div>
                   </div>

                  {/* Guardian Information Section */}
                  <div className="form-section">
                    <h3 className="section-title">
                      <span className="section-icon">👨‍👩‍👧‍👦</span>
                      Guardian Information
                    </h3>
                    
                                         <div className="form-grid">
                       <div className="form-group">
                         <label htmlFor="guardian_name">Guardian Name *</label>
                         <input
                           type="text"
                           id="guardian_name"
                           name="guardian_name"
                           value={newStudent.guardian_name}
                           onChange={handleStudentInputChange}
                           required
                           placeholder="Enter guardian's name"
                         />
                       </div>
                       
                       <div className="form-group">
                         <label htmlFor="guardian_phone">Guardian Phone *</label>
                         <input
                           type="tel"
                           id="guardian_phone"
                           name="guardian_phone"
                           value={newStudent.guardian_phone}
                           onChange={handleStudentInputChange}
                           required
                           placeholder="Enter 10-digit guardian's phone"
                           maxLength="10"
                           pattern="[6-9][0-9]{9}"
                         />
                       </div>
                       
                       <div className="form-group full-width">
                         <label htmlFor="guardian_address">Guardian Address *</label>
                         <textarea
                           id="guardian_address"
                           name="guardian_address"
                           value={newStudent.guardian_address}
                           onChange={handleStudentInputChange}
                           required
                           placeholder="Enter guardian's address"
                           rows="3"
                         />
                       </div>
                     </div>
                  </div>

                  {/* Form Actions */}
                  <div className="form-actions">
                    <button 
                      type="submit" 
                      className="submit-btn"
                      disabled={addingStudent}
                    >
                      {addingStudent ? (
                        <>
                          <span className="loading-spinner">⏳</span>
                          Adding Student...
                        </>
                      ) : (
                        <>
                          <span className="submit-icon">➕</span>
                          Add Student
                        </>
                      )}
                    </button>
                    
                    <button 
                      type="button" 
                      className="reset-btn"
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
                        password: '',
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
                      <span className="reset-icon">🔄</span>
                      Reset Form
                    </button>
                  </div>
                </form>
              </div>
            </div>
          )}
          
          {activeTab === 'overview' && (
            <div className="overview-section">
              {/* Tab Header with Refresh */}
              <div className="tab-header">
                <h2>Overview</h2>
                <button 
                  className={`refresh-btn ${overviewRefreshing ? 'refreshing' : ''}`}
                  onClick={refreshOverview}
                  disabled={overviewRefreshing}
                  title="Refresh Overview Data"
                >
                  {overviewRefreshing ? '🔄' : '↻'}
                </button>
              </div>

              {/* Main Stats Grid */}
              <div className={`stats-grid ${overviewRefreshing ? 'loading' : ''}`}>
                <div className="stat-card primary">
                  <div className="stat-icon">👥</div>
                  <div className="stat-content">
                    <h3>Total Students</h3>
                    <div className="stat-number">{students.length}</div>
                    <div className="stat-description">Registered students</div>
                  </div>
                </div>
                
                <div className="stat-card success">
                  <div className="stat-icon">🏢</div>
                  <div className="stat-content">
                    <h3>Total Rooms</h3>
                    <div className="stat-number">{rooms.length}</div>
                    <div className="stat-description">Available rooms</div>
                  </div>
                </div>
                
                <div className="stat-card warning">
                  <div className="stat-icon">⏳</div>
                  <div className="stat-content">
                    <h3>Pending Requests</h3>
                    <div className="stat-number">
                      {roomChangeRequests.filter(req => req.status === 'pending').length + 
                       personalDetailsRequests.filter(req => req.status === 'pending').length}
                    </div>
                    <div className="stat-description">Awaiting approval</div>
                  </div>
                </div>
                
                <div className="stat-card info">
                  <div className="stat-icon">🛏️</div>
                  <div className="stat-content">
                    <h3>Available Beds</h3>
                    <div className="stat-number">
                      {rooms.reduce((total, room) => total + (room.available_beds || 0), 0)}
                    </div>
                    <div className="stat-description">Ready for assignment</div>
                  </div>
                </div>
              </div>

              {/* Secondary Stats Grid */}
              <div className={`secondary-stats-grid ${overviewRefreshing ? 'loading' : ''}`}>
                <div className="stat-card secondary">
                  <div className="stat-icon">✅</div>
                  <div className="stat-content">
                    <h3>Assigned Students</h3>
                    <div className="stat-number">{students.filter(s => s.room_number || s.room_id).length}</div>
                    <div className="stat-description">Students with rooms</div>
                  </div>
                </div>
                
                <div className="stat-card secondary">
                  <div className="stat-icon">❌</div>
                  <div className="stat-content">
                    <h3>Unassigned Students</h3>
                    <div className="stat-number">{students.filter(s => !s.room_number && !s.room_id).length}</div>
                    <div className="stat-description">Need room assignment</div>
                  </div>
                </div>
                
                <div className="stat-card secondary">
                  <div className="stat-icon">📋</div>
                  <div className="stat-content">
                    <h3>Total Requests</h3>
                    <div className="stat-number">{roomChangeRequests.length + personalDetailsRequests.length}</div>
                    <div className="stat-description">All time requests</div>
                  </div>
                </div>
                
                <div className="stat-card secondary">
                  <div className="stat-icon">🆔</div>
                  <div className="stat-content">
                    <h3>Personal Details Requests</h3>
                    <div className="stat-number">{personalDetailsRequests.length}</div>
                    <div className="stat-description">Profile update requests</div>
                  </div>
                </div>
                
                <div className="stat-card secondary">
                  <div className="stat-icon">🏠</div>
                  <div className="stat-content">
                    <h3>Room Change Requests</h3>
                    <div className="stat-number">{roomChangeRequests.length}</div>
                    <div className="stat-description">Room transfer requests</div>
                  </div>
                </div>
                
                <div className="stat-card secondary">
                  <div className="stat-icon">🎯</div>
                  <div className="stat-content">
                    <h3>Occupancy Rate</h3>
                    <div className="stat-number">
                      {rooms.length > 0 ? Math.round(((rooms.reduce((total, room) => total + (room.capacity || 0), 0) - rooms.reduce((total, room) => total + (room.available_beds || 0), 0)) / rooms.reduce((total, room) => total + (room.capacity || 0), 0)) * 100) : 0}%
                    </div>
                    <div className="stat-description">Bed utilization</div>
                  </div>
                </div>
              </div>

              {/* Quick Actions */}
              <div className={`quick-actions ${overviewRefreshing ? 'loading' : ''}`}>
                <h3>🚀 Quick Actions</h3>
                <div className="actions-grid">
                  <button className="action-card" onClick={() => setActiveTab('rooms')}>
                    <div className="action-icon">🏠</div>
                    <div className="action-title">Assign Rooms</div>
                    <div className="action-description">Assign students to available rooms</div>
                  </button>
                  
                  <button className="action-card" onClick={() => setActiveTab('requests')}>
                    <div className="action-icon">📝</div>
                    <div className="action-title">Review Requests</div>
                    <div className="action-description">Approve or reject room change requests</div>
                  </button>
                  
                  <button className="action-card" onClick={() => setActiveTab('students')}>
                    <div className="action-icon">👥</div>
                    <div className="action-title">Manage Students</div>
                    <div className="action-description">View and manage student information</div>
                  </button>
                </div>
              </div>

              {/* Recent Activity */}
              <div className={`recent-activity ${overviewRefreshing ? 'loading' : ''}`}>
                <h3>📊 Recent Activity</h3>
                <div className="activity-list">
                  {(() => {
                    // Combine room change and personal details requests, then sort by date
                    const allRequests = [
                      ...roomChangeRequests.map(req => ({
                        ...req,
                        type: 'room_change',
                        date: new Date(req.requested_at)
                      })),
                      ...personalDetailsRequests.map(req => ({
                        ...req,
                        type: 'personal_details',
                        date: new Date(req.requested_at)
                      }))
                    ];
                    
                    return allRequests
                      .sort((a, b) => b.date - a.date)
                      .slice(0, 8)
                      .map(request => (
                        <div key={`${request.type}-${request.id}`} className="activity-item">
                          <div className="activity-icon">
                            {request.status === 'pending' ? '⏳' : request.status === 'approved' ? '✅' : '❌'}
                          </div>
                          <div className="activity-content">
                            <div className="activity-title">
                              {request.type === 'room_change' 
                                ? `${request.student_name} requested room change`
                                : `${request.student_name} requested personal details update`
                              }
                            </div>
                            <div className="activity-description">
                              {request.type === 'room_change' 
                                ? `${request.current_room || 'No room'} → Room ${request.requested_room}`
                                : `Phone, address, and guardian details`
                              } • 
                              <span className={`status-inline ${request.status}`}> {request.status}</span>
                            </div>
                            <div className="activity-time">
                              {request.date.toLocaleDateString()}
                            </div>
                          </div>
                        </div>
                      ));
                  })()}
                  {(roomChangeRequests.length === 0 && personalDetailsRequests.length === 0) && (
                    <div className="no-activity">
                      <div className="no-activity-icon">📭</div>
                      <div>No recent requests</div>
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}

          {activeTab === 'rooms' && (
            <div className="rooms-section">
              {/* Tab Header with Refresh */}
              <div className="tab-header">
                <h2>Room Management & Assignment</h2>
                <button 
                  className={`refresh-btn ${roomsRefreshing ? 'refreshing' : ''}`}
                  onClick={refreshRooms}
                  disabled={roomsRefreshing}
                  title="Refresh Rooms Data"
                >
                  {roomsRefreshing ? '🔄' : '↻'}
                </button>
              </div>
              
              {/* Room Assignment Form */}
              <div className={`room-assignment-container ${roomsRefreshing ? 'loading' : ''}`}>
                <h3>Assign Room to Student</h3>
                <form onSubmit={handleRoomAssignment} className="assignment-form">
                  <div className="selection-dropdowns">
                    <div className="form-group">
                      <label htmlFor="studentSelect">1. Select Student</label>
                      <select
                        id="studentSelect"
                        value={roomAssignment.studentId}
                        onChange={(e) => setRoomAssignment({
                          ...roomAssignment,
                          studentId: e.target.value,
                          roomId: '',
                          bedNumber: ''
                        })}
                        required
                      >
                        <option value="">-- Select Student --</option>
                        {allStudents.filter(student => !student.room_number && !student.room_id).map(student => (
                          <option key={student.id} value={student.roll_no}>
                            {student.full_name} - {student.roll_no} - Unassigned
                          </option>
                        ))}
                        {allStudents.filter(student => student.room_number || student.room_id).map(student => (
                          <option key={student.id} value={student.roll_no}>
                            {student.full_name} - {student.roll_no} - Assigned (Room {student.room_number || 'Unknown'})
                          </option>
                        ))}
                      </select>
                      
                      
                    </div>
                    
                    <div className="form-group">
                      <label htmlFor="roomSelect">2. Select Room</label>
                      <select
                        id="roomSelect"
                        value={roomAssignment.roomId}
                        onChange={handleRoomChange}
                        required
                        disabled={!roomAssignment.studentId}
                      >
                        <option value="">-- Select Room --</option>
                        {rooms.filter(room => (room.available_beds || 0) > 0).map(room => (
                          <option key={room.id} value={room.id}>
                            Room {room.room_number} - Floor {room.floor} ({room.available_beds} beds available)
                          </option>
                        ))}
                      </select>
                    </div>
                    
                    <div className="form-group">
                      <label htmlFor="bedSelect">3. Select Bed</label>
                      <select
                        id="bedSelect"
                        value={roomAssignment.bedNumber}
                        onChange={(e) => setRoomAssignment({
                          ...roomAssignment,
                          bedNumber: e.target.value
                        })}
                        required
                        disabled={!roomAssignment.roomId || bedsLoading}
                      >
                        <option value="">-- Select Bed --</option>
                        {availableBeds.map(bedNumber => (
                          <option key={bedNumber} value={bedNumber}>
                            Bed {bedNumber}
                          </option>
                        ))}
                      </select>
                      {bedsLoading && <span className="loading-text">Loading beds...</span>}
                    </div>
                  </div>

                  {/* Current Room Details */}
                  {roomAssignment.studentId && (
                    <div className="student-room-details">
                      {(() => {
                        const selectedStudent = allStudents.find(s => s.roll_no === roomAssignment.studentId);
                        if (!selectedStudent) return null;
                        
                        if (selectedStudent.room_number || selectedStudent.room_id) {
                          return (
                            <div className="current-assignment-card">
                              <h4>📍 Current Room Assignment</h4>
                              <div className="assignment-details">
                                <div className="detail-row">
                                  <span className="label">Student:</span>
                                  <span className="value">{selectedStudent.full_name}</span>
                                </div>
                                <div className="detail-row">
                                  <span className="label">Roll Number:</span>
                                  <span className="value">{selectedStudent.roll_no}</span>
                                </div>
                                <div className="detail-row">
                                  <span className="label">Current Room:</span>
                                  <span className="value highlight">Room {selectedStudent.room_number || 'Unknown'}</span>
                                </div>
                                {selectedStudent.bed_number && (
                                  <div className="detail-row">
                                    <span className="label">Current Bed:</span>
                                    <span className="value highlight">Bed {selectedStudent.bed_number}</span>
                                  </div>
                                )}
                                <div className="detail-row">
                                  <span className="label">Stream:</span>
                                  <span className="value">{selectedStudent.stream || 'Not specified'}</span>
                                </div>
                                <div className="detail-row">
                                  <span className="label">Branch:</span>
                                  <span className="value">{selectedStudent.branch || 'Not specified'}</span>
                                </div>
                              </div>
                              <div className="assignment-note">
                                <strong>Note:</strong> This student is already assigned to a room. Proceeding will reassign them to a new room.
                              </div>
                            </div>
                          );
                        } else {
                          return (
                            <div className="no-assignment-card">
                              <h4>✅ Student Status</h4>
                              <div className="assignment-details">
                                <div className="detail-row">
                                  <span className="label">Student:</span>
                                  <span className="value">{selectedStudent.full_name}</span>
                                </div>
                                <div className="detail-row">
                                  <span className="label">Roll Number:</span>
                                  <span className="value">{selectedStudent.roll_no}</span>
                                </div>
                                <div className="detail-row">
                                  <span className="label">Room Status:</span>
                                  <span className="value unassigned">Not Assigned</span>
                                </div>
                                <div className="detail-row">
                                  <span className="label">Stream:</span>
                                  <span className="value">{selectedStudent.stream || 'Not specified'}</span>
                                </div>
                                <div className="detail-row">
                                  <span className="label">Branch:</span>
                                  <span className="value">{selectedStudent.branch || 'Not specified'}</span>
                                </div>
                              </div>
                              <div className="assignment-note success">
                                <strong>Ready:</strong> This student is available for room assignment.
                              </div>
                            </div>
                          );
                        }
                      })()}
                    </div>
                  )}
                  
                  <button 
                    type="submit" 
                    className="assign-button"
                    disabled={!roomAssignment.studentId || !roomAssignment.roomId || !roomAssignment.bedNumber}
                  >
                    Assign Room
                  </button>
                </form>
              </div>

              {/* Rooms Overview */}
              <div className={`rooms-overview ${roomsRefreshing ? 'loading' : ''}`}>
                <h3>All Rooms</h3>
                <div className="rooms-grid">
                  {rooms.map(room => (
                    <div key={room.id} className="room-card">
                      <div className="room-header">
                        <h4>Room {room.room_number}</h4>
                        <span className="floor-badge">Floor {room.floor}</span>
                      </div>
                      <div className="room-details">
                        <p><strong>Type:</strong> {room.room_type}</p>
                        <p><strong>Capacity:</strong> {room.capacity} beds</p>
                        <p><strong>Occupied:</strong> {(room.capacity || 0) - (room.available_beds || 0)} beds</p>
                        <p><strong>Available:</strong> {room.available_beds || 0} beds</p>
                      </div>
                      <div className="room-status">
                        {(room.available_beds || 0) === 0 ? (
                          <span className="status-full">Full</span>
                        ) : (
                          <span className="status-available">Available</span>
                        )}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          )}

          {activeTab === 'requests' && (
            <div className="requests-section">
              {/* Tab Header with Refresh */}
              <div className="tab-header">
                <h2>All Requests</h2>
                <button 
                  className={`refresh-btn ${requestsRefreshing ? 'refreshing' : ''}`}
                  onClick={refreshRequests}
                  disabled={requestsRefreshing}
                  title="Refresh Requests Data"
                >
                  {requestsRefreshing ? '🔄' : '↻'}
                </button>
              </div>
              
              <div className={`requests-table-container ${requestsRefreshing ? 'loading' : ''}`}>
                <table className="requests-table">
                  <thead>
                    <tr>
                      <th>Student</th>
                      <th>Current Room</th>
                      <th>Requested Room</th>
                      <th>Bed</th>
                      <th>Reason</th>
                      <th>Date</th>
                      <th>Status/Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {roomChangeRequests.map(request => (
                      <tr key={request.id}>
                        <td>{request.student_name}</td>
                        <td>{request.current_room || 'None'}</td>
                        <td>Room {request.requested_room}</td>
                        <td>Bed {request.requested_bed_number}</td>
                        <td className="reason-cell">{request.reason}</td>
                        <td>{new Date(request.requested_at).toLocaleDateString()}</td>
                        <td className="status-action-cell">
                          {request.status === 'pending' ? (
                            <div className="action-buttons">
                              <button 
                                className="action-btn approve-btn"
                                onClick={() => handleRequestAction(request, 'approve')}
                                title="Approve Request"
                              >
                                ✅
                              </button>
                              <button 
                                className="action-btn reject-btn"
                                onClick={() => handleRequestAction(request, 'reject')}
                                title="Reject Request"
                              >
                                ❌
                              </button>
                            </div>
                          ) : (
                            <span className={`status-badge ${request.status}`}>
                              {request.status.charAt(0).toUpperCase() + request.status.slice(1)}
                            </span>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
                
                {roomChangeRequests.length === 0 && (
                  <div className="no-requests">
                    <p>No room change requests found.</p>
                  </div>
                )}
              </div>

              {/* Personal Details Update Requests Section */}
              <div className="personal-details-requests-section">
                <h3>Personal Details Update Requests</h3>
                
                <div className={`requests-table-container ${requestsRefreshing ? 'loading' : ''}`}>
                  <table className="requests-table">
                    <thead>
                      <tr>
                        <th>Student</th>
                        <th>Roll No</th>
                        <th>Phone</th>
                        <th>Address</th>
                        <th>Guardian Info</th>
                        <th>Date</th>
                        <th>Status/Action</th>
                      </tr>
                    </thead>
                    <tbody>
                      {personalDetailsRequests.map(request => (
                        <tr key={request.id}>
                          <td>{request.student_name}</td>
                          <td>{request.roll_no}</td>
                          <td>{request.phone || 'Not updated'}</td>
                          <td className="address-cell">
                            {[
                              request.address_line1,
                              request.address_line2,
                              request.city,
                              request.state,
                              request.postal_code
                            ].filter(Boolean).join(', ') || 'Not updated'}
                          </td>
                          <td className="guardian-cell">
                            {[
                              request.guardian_name,
                              request.guardian_phone,
                              request.guardian_address
                            ].filter(Boolean).join(', ') || 'Not updated'}
                          </td>
                          <td>{new Date(request.requested_at).toLocaleDateString()}</td>
                          <td className="status-action-cell">
                            {request.status === 'pending' ? (
                              <div className="action-buttons">
                                <button 
                                  className="action-btn approve-btn"
                                  onClick={() => handlePersonalDetailsAction(request, 'approve')}
                                  title="Approve Request"
                                >
                                  ✅
                                </button>
                                <button 
                                  className="action-btn reject-btn"
                                  onClick={() => handlePersonalDetailsAction(request, 'reject')}
                                  title="Reject Request"
                                >
                                  ❌
                                </button>
                              </div>
                            ) : (
                              <div>
                                <span className={`status-badge ${request.status}`}>
                                  {request.status.charAt(0).toUpperCase() + request.status.slice(1)}
                                </span>
                                {request.warden_comments && (
                                  <div className="warden-comments" title={request.warden_comments}>
                                    💬 Comments
                                  </div>
                                )}
                              </div>
                            )}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                  
                  {personalDetailsRequests.length === 0 && (
                    <div className="no-requests">
                      <p>No personal details update requests found.</p>
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}
        </main>
      </div>

      {/* Enhanced Student View Modal */}
      {showStudentModal && selectedStudent && (
        <div className="modal-overlay" onClick={handleCloseStudentModal}>
          <div className="modal-content enhanced-student-modal" onClick={(e) => e.stopPropagation()}>
            {/* Modal Header */}
            <div className="enhanced-modal-header">
              <div className="student-avatar">
                <div className="avatar-circle">
                  {selectedStudent.full_name?.charAt(0).toUpperCase()}
                </div>
              </div>
              <div className="header-info">
                <h2 className="student-modal-name">{selectedStudent.full_name}</h2>
                <div className="student-badge-info">
                  <span className="roll-badge">{selectedStudent.roll_no}</span>
                  <span className="stream-badge">{selectedStudent.stream} - {selectedStudent.branch}</span>
                </div>
              </div>
              <button className="enhanced-close-btn" onClick={handleCloseStudentModal} title="Close">
                ✕
              </button>
            </div>

            {/* Modal Content */}
            <div className="enhanced-modal-content">
              {/* Personal Information Section */}
              <div className="info-section personal-info">
                <div className="section-header">
                  <span className="section-icon">👤</span>
                  <h3>Personal Information</h3>
                </div>
                <div className="info-grid">
                  <div className="info-item">
                    <label>📧 Email</label>
                    <span>{selectedStudent.email || 'Not provided'}</span>
                  </div>
                  <div className="info-item">
                    <label>📱 Phone</label>
                    <span>{selectedStudent.phone || 'Not provided'}</span>
                  </div>
                  <div className="info-item">
                    <label>🎂 Date of Birth</label>
                    <span>{selectedStudent.date_of_birth || 'Not provided'}</span>
                  </div>
                  <div className="info-item">
                    <label>👫 Gender</label>
                    <span>{selectedStudent.gender || 'Not specified'}</span>
                  </div>
                  <div className="info-item full-width">
                    <label>🆔 Aadhaar ID</label>
                    <span>{selectedStudent.aadhaar_id || 'Not provided'}</span>
                  </div>
                </div>
              </div>

              {/* Academic Information Section */}
              <div className="info-section academic-info">
                <div className="section-header">
                  <span className="section-icon">🎓</span>
                  <h3>Academic Information</h3>
                </div>
                <div className="info-grid">
                  <div className="info-item">
                    <label>📚 Stream</label>
                    <span>{selectedStudent.stream || 'Not specified'}</span>
                  </div>
                  <div className="info-item">
                    <label>🏛️ Branch</label>
                    <span>{selectedStudent.branch || 'Not specified'}</span>
                  </div>
                  <div className="info-item">
                    <label>📋 Roll Number</label>
                    <span className="highlight-text">{selectedStudent.roll_no}</span>
                  </div>
                </div>
              </div>

              {/* Room Assignment Section */}
              <div className="info-section room-info">
                <div className="section-header">
                  <span className="section-icon">🏠</span>
                  <h3>Room Assignment</h3>
                </div>
                <div className="room-assignment-status">
                  {selectedStudent.room_number ? (
                    <div className="assigned-room">
                      <div className="room-details">
                        <div className="room-number">
                          <span className="room-label">Room</span>
                          <span className="room-value">{selectedStudent.room_number}</span>
                        </div>
                        <div className="bed-number">
                          <span className="bed-label">Bed</span>
                          <span className="bed-value">{selectedStudent.bed_number || 'TBD'}</span>
                        </div>
                      </div>
                      <div className="assignment-status assigned">
                        <span className="status-icon">✅</span>
                        <span>Room Assigned</span>
                      </div>
                    </div>
                  ) : (
                    <div className="unassigned-room">
                      <div className="assignment-status unassigned">
                        <span className="status-icon">❌</span>
                        <span>No Room Assigned</span>
                      </div>
                      <p className="unassigned-note">This student needs to be assigned to a room.</p>
                    </div>
                  )}
                </div>
              </div>
            </div>

            {/* Modal Footer */}
            <div className="enhanced-modal-footer">
              <button className="close-button" onClick={handleCloseStudentModal}>
                <span className="button-icon">👍</span>
                Got it
              </button>
            </div>
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
                onClick={() => console.log('Delete functionality would go here')}
                disabled={studentsLoading}
              >
                {studentsLoading ? 'Deleting...' : 'Delete Student'}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Comments Modal for Personal Details Requests */}
      {showCommentsModal && selectedRequest && (
        <div className="modal-overlay">
          <div className="modal-content comments-modal">
            <h3>{actionType === 'approve' ? 'Approve' : 'Reject'} Personal Details Update</h3>
            <div className="request-summary">
              <p><strong>Student:</strong> {selectedRequest.student_name}</p>
              <p><strong>Roll No:</strong> {selectedRequest.roll_no}</p>
              <div className="details-grid">
                {selectedRequest.phone && (
                  <div className="detail-item">
                    <strong>Phone:</strong> {selectedRequest.phone}
                  </div>
                )}
                {(selectedRequest.address_line1 || selectedRequest.city) && (
                  <div className="detail-item">
                    <strong>Address:</strong> {[
                      selectedRequest.address_line1,
                      selectedRequest.address_line2,
                      selectedRequest.city,
                      selectedRequest.state,
                      selectedRequest.postal_code
                    ].filter(Boolean).join(', ')}
                  </div>
                )}
                {selectedRequest.guardian_name && (
                  <div className="detail-item">
                    <strong>Guardian:</strong> {selectedRequest.guardian_name}
                    {selectedRequest.guardian_phone && ` (${selectedRequest.guardian_phone})`}
                  </div>
                )}
              </div>
            </div>
            
            <div className="comment-section">
              <label htmlFor="wardenComments">Warden Comments:</label>
              <textarea
                id="wardenComments"
                value={wardenComments}
                onChange={(e) => setWardenComments(e.target.value)}
                placeholder={`Enter your comments for ${actionType === 'approve' ? 'approving' : 'rejecting'} this request...`}
                rows="4"
                className="comment-textarea"
              />
            </div>
            
            <div className="modal-actions">
              <button 
                className="cancel-button" 
                onClick={() => {
                  setShowCommentsModal(false);
                  setSelectedRequest(null);
                  setActionType('');
                  setWardenComments('');
                }}
              >
                Cancel
              </button>
              <button 
                className={`confirm-button ${actionType === 'approve' ? 'approve' : 'reject'}`}
                onClick={submitPersonalDetailsAction}
              >
                {actionType === 'approve' ? 'Approve Request' : 'Reject Request'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default WardenDashboard; 