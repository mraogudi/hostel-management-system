import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import './StudentDashboard.css';

const StudentDashboard = () => {
  const { user, logout, apiCall } = useAuth();
  const [activeTab, setActiveTab] = useState('room');
  const [roomInfo, setRoomInfo] = useState(null);
  const [foodMenu, setFoodMenu] = useState([]);
  const [rooms, setRooms] = useState([]);
  const [roomChangeRequest, setRoomChangeRequest] = useState({
    requested_room_id: '',
    requested_bed_number: '',
    reason: ''
  });
  const [availableBeds, setAvailableBeds] = useState([]);
  const [bedsLoading, setBedsLoading] = useState(false);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    
    // Fetch room info
    const roomResult = await apiCall('GET', '/api/student/my-room');
    if (roomResult.success) {
      setRoomInfo(roomResult.data);
    }

    // Fetch food menu
    const menuResult = await apiCall('GET', '/api/food-menu');
    if (menuResult.success) {
      setFoodMenu(menuResult.data);
    }

    // Fetch available rooms for room change
    const roomsResult = await apiCall('GET', '/api/rooms');
    if (roomsResult.success) {
      setRooms(roomsResult.data);
    }

    setLoading(false);
  };

  const handleRoomChangeRequest = async (e) => {
    e.preventDefault();
    
    // Create request object with backend-expected field names
    const requestData = {
      requestedRoomId: roomChangeRequest.requested_room_id,
      requestedBedNumber: parseInt(roomChangeRequest.requested_bed_number),
      reason: roomChangeRequest.reason
    };
    
    const result = await apiCall('POST', '/api/student/room-change-request', requestData);
    
    if (result.success) {
      setMessage('Room change request submitted successfully!');
      setRoomChangeRequest({ requested_room_id: '', requested_bed_number: '', reason: '' });
      setAvailableBeds([]);
    } else {
      setMessage(`Error: ${result.error}`);
    }

    setTimeout(() => setMessage(''), 5000);
  };

  // Function to get available beds for a selected room
  const getAvailableBeds = async (roomId) => {
    if (!roomId) return [];
    
    try {
      console.log('Fetching beds for room change request:', roomId);
      const result = await apiCall('GET', `/api/rooms/${roomId}`);
      
      if (result.success && result.data && result.data.beds) {
        const availableBeds = result.data.beds
          .filter(bed => bed.status === 'available')
          .map(bed => bed.bed_number)
          .sort((a, b) => a - b);
        
        console.log('Available beds for room change:', availableBeds);
        return availableBeds;
      }
      return [];
    } catch (error) {
      console.error('Error fetching beds for room change:', error);
      return [];
    }
  };

  // Handle room selection for room change
  const handleRoomChangeSelection = async (roomId) => {
    setRoomChangeRequest({
      ...roomChangeRequest,
      requested_room_id: roomId,
      requested_bed_number: ''
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

  const groupMenuByDay = () => {
    const grouped = {};
    foodMenu.forEach(item => {
      if (!grouped[item.day_of_week]) {
        grouped[item.day_of_week] = {};
      }
      grouped[item.day_of_week][item.meal_type] = item.items;
    });
    return grouped;
  };

  if (loading) {
    return <div className="loading">Loading dashboard...</div>;
  }

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="header-content">
          <h1>Student Portal</h1>
          <div className="user-info">
            <span>Welcome, {user.full_name}</span>
            <button onClick={logout} className="logout-button">Logout</button>
          </div>
        </div>
      </header>

      <div className="dashboard-content">
        <nav className="dashboard-nav">
          <button 
            className={`nav-button ${activeTab === 'room' ? 'active' : ''}`}
            onClick={() => setActiveTab('room')}
          >
            My Room
          </button>
          <button 
            className={`nav-button ${activeTab === 'menu' ? 'active' : ''}`}
            onClick={() => setActiveTab('menu')}
          >
            Food Menu
          </button>
          <button 
            className={`nav-button ${activeTab === 'request' ? 'active' : ''}`}
            onClick={() => setActiveTab('request')}
          >
            Room Change
          </button>
        </nav>

        <main className="dashboard-main">
          {message && <div className="message">{message}</div>}

          {activeTab === 'room' && (
            <div className="room-info-section">
              <div className="section-header">
                <h2>My Profile & Room Details</h2>
                <button 
                  className="refresh-btn"
                  onClick={() => fetchData()}
                  title="Refresh Room Data"
                >
                  🔄
                </button>
              </div>
              
              {/* Student Profile Section */}
              <div className="profile-card">
                <h3>Student Information</h3>
                <div className="profile-details">
                  <div className="detail-row">
                    <div className="detail-item">
                      <strong>Full Name:</strong> {user.full_name}
                    </div>
                    <div className="detail-item">
                      <strong>Roll Number:</strong> {user.roll_no || 'Not assigned'}
                    </div>
                  </div>
                  <div className="detail-row">
                    <div className="detail-item">
                      <strong>Stream:</strong> {user.stream || 'Not specified'}
                    </div>
                    <div className="detail-item">
                      <strong>Branch:</strong> {user.branch || 'Not specified'}
                    </div>
                  </div>
                  <div className="detail-row">
                    <div className="detail-item">
                      <strong>Gender:</strong> {user.gender || 'Not specified'}
                    </div>
                    <div className="detail-item">
                      <strong>Email:</strong> {user.email || 'Not provided'}
                    </div>
                  </div>
                  <div className="detail-row">
                    <div className="detail-item">
                      <strong>Phone:</strong> {user.phone || 'Not provided'}
                    </div>
                    <div className="detail-item">
                      <strong>Date of Birth:</strong> {user.date_of_birth ? new Date(user.date_of_birth).toLocaleDateString() : 'Not provided'}
                    </div>
                  </div>
                </div>
              </div>

              {/* Room Information Section */}
              {roomInfo ? (
                <div className="room-card">
                  <div className="room-header">
                    <h3>Room {roomInfo.room_number}</h3>
                    <span className="floor-badge">Floor {roomInfo.floor}</span>
                  </div>
                  <div className="room-details">
                    <div className="detail-item">
                      <strong>Bed Number:</strong> {roomInfo.bed_number}
                    </div>
                    <div className="detail-item">
                      <strong>Room Type:</strong> {roomInfo.room_type}
                    </div>
                    <div className="detail-item">
                      <strong>Capacity:</strong> {roomInfo.capacity} beds
                    </div>
                  </div>
                  
                  {roomInfo.roommates && roomInfo.roommates.length > 0 && (
                    <div className="roommates-section">
                      <h4>Roommates</h4>
                      <ul className="roommates-list">
                        {roomInfo.roommates.map((roommate, index) => (
                          <li key={index}>{roommate.full_name}</li>
                        ))}
                      </ul>
                    </div>
                  )}
                </div>
              ) : (
                <div className="no-room">
                  <p>No room assigned yet. Please contact the warden.</p>
                </div>
              )}
            </div>
          )}

          {activeTab === 'menu' && (
            <div className="menu-section">
              <h2>Food Menu</h2>
              <div className="menu-grid">
                {Object.entries(groupMenuByDay()).map(([day, meals]) => (
                  <div key={day} className="day-card">
                    <h3>{day}</h3>
                    {Object.entries(meals).map(([mealType, items]) => (
                      <div key={mealType} className="meal-item">
                        <h4>{mealType.charAt(0).toUpperCase() + mealType.slice(1)}</h4>
                        <p>{items}</p>
                      </div>
                    ))}
                  </div>
                ))}
              </div>
            </div>
          )}

          {activeTab === 'request' && (
            <div className="request-section">
              <div className="section-header">
                <h2>Request Room Change</h2>
                <button 
                  className="refresh-btn"
                  onClick={() => fetchData()}
                  title="Refresh Available Rooms"
                >
                  🔄 
                </button>
              </div>
              
              {/* Current Room Details */}
              {roomInfo && (
                <div className="current-room-info">
                  <h3>Current Room Details</h3>
                  <div className="current-room-card">
                    <div className="room-header">
                      <h4>Room {roomInfo.room_number}</h4>
                      <span className="room-details">Floor {roomInfo.floor} • {roomInfo.room_type}</span>
                    </div>
                    <div className="room-details-grid">
                      <div className="detail-item">
                        <span className="label">Your Bed:</span>
                        <span className="value">Bed {roomInfo.bed_number}</span>
                      </div>
                      <div className="detail-item">
                        <span className="label">Capacity:</span>
                        <span className="value">{roomInfo.capacity} beds</span>
                      </div>
                      <div className="detail-item">
                        <span className="label">Roommates:</span>
                        <span className="value">
                          {roomInfo.roommates && roomInfo.roommates.length > 0 
                            ? roomInfo.roommates.map(roommate => roommate.full_name).join(', ')
                            : 'No roommates'
                          }
                        </span>
                      </div>
                    </div>
                  </div>
                </div>
              )}

              {message && (
                <div className={`message ${message.includes('success') ? 'success' : 'error'}`}>
                  {message}
                </div>
              )}

              <form onSubmit={handleRoomChangeRequest} className="request-form">
                <div className="form-group">
                  <label htmlFor="requested_room">Select New Room:</label>
                  <select
                    id="requested_room"
                    value={roomChangeRequest.requested_room_id}
                    onChange={(e) => handleRoomChangeSelection(e.target.value)}
                    required
                  >
                    <option value="">Choose a room...</option>
                    {rooms
                      .filter(room => room.available_beds > 0 && room.id !== roomInfo?.id)
                      .map(room => (
                        <option key={room.id} value={room.id}>
                          Room {room.room_number} - Floor {room.floor} 
                          ({room.available_beds} beds available)
                        </option>
                      ))}
                  </select>
                </div>

                {/* Bed Selection */}
                {roomChangeRequest.requested_room_id && (
                  <div className="form-group">
                    <label htmlFor="requested_bed">Select Preferred Bed:</label>
                    <select
                      id="requested_bed"
                      value={roomChangeRequest.requested_bed_number}
                      onChange={(e) => setRoomChangeRequest({
                        ...roomChangeRequest,
                        requested_bed_number: e.target.value
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
                      ) : !bedsLoading && availableBeds.length === 0 && roomChangeRequest.requested_room_id ? (
                        <option value="" disabled>No available beds</option>
                      ) : null}
                    </select>
                    {!bedsLoading && availableBeds.length === 0 && roomChangeRequest.requested_room_id && (
                      <small className="field-note error">No available beds in this room</small>
                    )}
                  </div>
                )}

                <div className="form-group">
                  <label htmlFor="reason">Reason for Change:</label>
                  <textarea
                    id="reason"
                    value={roomChangeRequest.reason}
                    onChange={(e) => setRoomChangeRequest({
                      ...roomChangeRequest,
                      reason: e.target.value
                    })}
                    placeholder="Please explain why you want to change rooms..."
                    rows={4}
                    required
                  />
                </div>

                <button 
                  type="submit" 
                  className="submit-button"
                  disabled={roomChangeRequest.requested_room_id && availableBeds.length === 0}
                >
                  Submit Request
                </button>
              </form>
            </div>
          )}
        </main>
      </div>
    </div>
  );
};

export default StudentDashboard; 