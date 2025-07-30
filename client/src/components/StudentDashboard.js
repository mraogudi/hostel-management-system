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
    reason: ''
  });
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
    
    const result = await apiCall('POST', '/api/student/room-change-request', roomChangeRequest);
    
    if (result.success) {
      setMessage('Room change request submitted successfully!');
      setRoomChangeRequest({ requested_room_id: '', reason: '' });
    } else {
      setMessage(`Error: ${result.error}`);
    }

    setTimeout(() => setMessage(''), 5000);
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
              <h2>My Profile & Room Details</h2>
              
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
              <h2>Request Room Change</h2>
              <form onSubmit={handleRoomChangeRequest} className="request-form">
                <div className="form-group">
                  <label htmlFor="requested_room">Select New Room:</label>
                  <select
                    id="requested_room"
                    value={roomChangeRequest.requested_room_id}
                    onChange={(e) => setRoomChangeRequest({
                      ...roomChangeRequest,
                      requested_room_id: e.target.value
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

                <button type="submit" className="submit-button">
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