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
  const [selectedRoomDetails, setSelectedRoomDetails] = useState(null);
  const [showBedLayout, setShowBedLayout] = useState(false);
  const [activeFilter, setActiveFilter] = useState('all');

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

    // Fetch all rooms (including occupied ones) for room change
    const roomsResult = await apiCall('GET', '/api/rooms');
    if (roomsResult.success) {
      console.log('Fetched rooms data:', roomsResult.data);
      setRooms(roomsResult.data);
    } else {
      console.error('Failed to fetch rooms:', roomsResult.error);
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

  // Handle room selection for visual interface
  const handleRoomSelection = async (room) => {
    const isCurrentRoom = room.id === roomInfo?.id;
    const isFullyOccupied = room.available_beds === 0;
    
    // Allow viewing any room, but only allow selection of available rooms
    if (!isCurrentRoom && !isFullyOccupied) {
      setRoomChangeRequest({
        ...roomChangeRequest,
        requested_room_id: room.id,
        requested_bed_number: ''
      });
    }

    setBedsLoading(true);
    try {
      const result = await apiCall('GET', `/api/rooms/${room.id}`);
      if (result.success && result.data) {
        setSelectedRoomDetails(result.data);
        setShowBedLayout(true);
        if (!isCurrentRoom && !isFullyOccupied) {
          const beds = await getAvailableBeds(room.id);
          setAvailableBeds(beds);
        }
      }
    } catch (error) {
      console.error('Error fetching room details:', error);
    }
    setBedsLoading(false);
  };

  // Handle bed selection in visual interface
  const handleBedSelection = (bedNumber) => {
    setRoomChangeRequest({
      ...roomChangeRequest,
      requested_bed_number: bedNumber
    });
  };

  // Handle room selection for room change (legacy)
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

  // Visual Components
  const RoomCard = ({ room }) => {
    const isSelected = roomChangeRequest.requested_room_id === room.id;
    const isCurrentRoom = room.id === roomInfo?.id;
    const isFullyOccupied = room.available_beds === 0;
    const isUnavailable = isFullyOccupied || isCurrentRoom;
    const occupiedBeds = room.capacity - room.available_beds;
    
    return (
      <div 
        className={`room-card-visual ${isSelected ? 'selected' : ''} ${isUnavailable ? 'unavailable' : ''} ${isCurrentRoom ? 'current-room' : ''}`}
        onClick={() => handleRoomSelection(room)}
        style={{ cursor: 'pointer' }}
      >
        <div className="room-card-header">
          <h4>Room {room.room_number}</h4>
          <span className="floor-tag">Floor {room.floor}</span>
        </div>
        <div className="room-card-info">
          <div className="info-item">
            <span className="label">Type:</span>
            <span className="value">{room.room_type}</span>
          </div>
          <div className="info-item">
            <span className="label">Capacity:</span>
            <span className="value">{room.capacity} beds</span>
          </div>
          <div className="info-item">
            <span className="label">Occupied:</span>
            <span className={`value ${occupiedBeds > 0 ? 'occupied' : 'empty'}`}>
              {occupiedBeds} / {room.capacity} beds
            </span>
          </div>
          <div className="info-item">
            <span className="label">Available:</span>
            <span className={`value ${room.available_beds === 0 ? 'unavailable' : 'available'}`}>
              {room.available_beds} beds
            </span>
          </div>
          
          {/* Detailed bed breakdown for all rooms */}
          <div className="bed-breakdown">
            <span className="breakdown-title">Bed Status Overview:</span>
            <div className="bed-status-indicators">
              {occupiedBeds > 0 && (
                <span className="bed-indicator occupied">
                  <span className="indicator-dot occupied"></span>
                  {occupiedBeds} Occupied
                </span>
              )}
              {room.available_beds > 0 && (
                <span className="bed-indicator available">
                  <span className="indicator-dot available"></span>
                  {room.available_beds} Available
                </span>
              )}
              {occupiedBeds === 0 && (
                <span className="bed-indicator all-available">
                  <span className="indicator-dot available"></span>
                  All {room.capacity} beds available
                </span>
              )}
            </div>
            
            {/* Capacity visualization */}
            <div className="bed-preview">
              <span className="preview-title">Capacity:</span>
              <div className="bed-icons">
                {Array.from({ length: room.capacity }, (_, index) => {
                  const bedNumber = index + 1;
                  // Generic representation - red for occupied count, green for available
                  const isOccupied = index < occupiedBeds;
                  return (
                    <span
                      key={bedNumber}
                      className={`mini-bed ${isOccupied ? 'occupied' : 'available'}`}
                      title={`${isOccupied ? 'Occupied bed' : 'Available bed'} (Click room to see specific bed numbers)`}
                    >
                      {bedNumber}
                    </span>
                  );
                })}
              </div>
              <small className="preview-note">Click room to see actual bed numbers and details</small>
            </div>
          </div>
        </div>
        <div className="room-card-status">
          {isCurrentRoom ? (
            <span className="status-badge current">Your Room</span>
          ) : isFullyOccupied ? (
            <span className="status-badge unavailable">Full</span>
          ) : occupiedBeds > 0 ? (
            <span className="status-badge partial">Partially Occupied</span>
          ) : (
            <span className="status-badge available">Available</span>
          )}
        </div>
        
        {/* Occupancy indicator */}
        <div className="occupancy-indicator">
          <div className="occupancy-bar">
            <div 
              className="occupancy-fill" 
              style={{ width: `${(occupiedBeds / room.capacity) * 100}%` }}
            ></div>
          </div>
          <span className="occupancy-text">
            {Math.round((occupiedBeds / room.capacity) * 100)}% occupied
          </span>
        </div>
      </div>
    );
  };

  const BedLayout = ({ roomDetails }) => {
    if (!roomDetails || !roomDetails.beds) return null;

    const isCurrentRoom = roomDetails.id === roomInfo?.id;
    const isFullyOccupied = roomDetails.beds.filter(bed => bed.status === 'available').length === 0;
    const isViewOnly = isCurrentRoom || isFullyOccupied;

    const createBedGrid = () => {
      const beds = roomDetails.beds.sort((a, b) => a.bed_number - b.bed_number);
      const bedsPerRow = Math.ceil(Math.sqrt(roomDetails.capacity));
      const rows = [];
      
      for (let i = 0; i < beds.length; i += bedsPerRow) {
        rows.push(beds.slice(i, i + bedsPerRow));
      }
      
      return rows;
    };

    const getBedStatus = (bed) => {
      if (bed.status === 'occupied') return 'occupied';
      if (!isViewOnly && roomChangeRequest.requested_bed_number === bed.bed_number) return 'selected';
      return 'available';
    };

    return (
      <div className="bed-layout-container">
        <div className="bed-layout-header">
          <div>
            <h3>Room {roomDetails.room_number} - Floor {roomDetails.floor}</h3>
            <div className="room-summary">
              <span className="summary-item">
                <strong>Type:</strong> {roomDetails.room_type}
              </span>
              <span className="summary-item">
                <strong>Capacity:</strong> {roomDetails.capacity} beds
              </span>
              <span className="summary-item">
                <strong>Occupied:</strong> {roomDetails.beds.filter(bed => bed.status === 'occupied').length} beds
              </span>
              <span className="summary-item">
                <strong>Available:</strong> {roomDetails.beds.filter(bed => bed.status === 'available').length} beds
              </span>
            </div>
            {isCurrentRoom && <span className="room-mode-indicator current">Your Current Room</span>}
            {isFullyOccupied && !isCurrentRoom && <span className="room-mode-indicator occupied">Fully Occupied - View Only</span>}
            {!isViewOnly && <span className="room-mode-indicator selectable">Select a bed to continue</span>}
          </div>
          <button 
            className="close-layout-btn"
            onClick={() => {
              setShowBedLayout(false);
              setSelectedRoomDetails(null);
              if (!isViewOnly) {
                setRoomChangeRequest({
                  ...roomChangeRequest,
                  requested_room_id: '',
                  requested_bed_number: ''
                });
              }
            }}
          >
            ✕
          </button>
        </div>
        
        <div className="bed-grid">
          {createBedGrid().map((row, rowIndex) => (
            <div key={rowIndex} className="bed-row">
              {row.map((bed) => (
                <div
                  key={bed.bed_number}
                  className={`bed-seat ${getBedStatus(bed)}`}
                  onClick={() => !isViewOnly && bed.status === 'available' && handleBedSelection(bed.bed_number)}
                  title={
                    bed.status === 'occupied' 
                      ? `Bed ${bed.bed_number} - OCCUPIED by ${bed.student_name || 'Unknown Student'}`
                      : isViewOnly
                      ? `Bed ${bed.bed_number} - Available (View Only Mode)`
                      : `Bed ${bed.bed_number} - AVAILABLE - Click to select`
                  }
                  style={{ cursor: (!isViewOnly && bed.status === 'available') ? 'pointer' : 'default' }}
                >
                  <div className="bed-number">#{bed.bed_number}</div>
                  <div className="bed-status-label">
                    {bed.status === 'occupied' ? (
                      <span className="status-occupied">OCCUPIED</span>
                    ) : (
                      <span className="status-available">AVAILABLE</span>
                    )}
                  </div>
                  {bed.status === 'occupied' && (
                    <div className="occupant-name">
                      {bed.student_name ? bed.student_name.split(' ')[0] : 'Student'}
                    </div>
                  )}
                </div>
              ))}
            </div>
          ))}
        </div>

        {/* Detailed bed status breakdown */}
        <div className="bed-details-section">
          <h4>Bed-by-Bed Status:</h4>
          <div className="bed-status-list">
            {roomDetails.beds
              .sort((a, b) => a.bed_number - b.bed_number)
              .map(bed => (
                <div key={bed.bed_number} className="bed-status-item">
                  <span className="bed-label">Bed #{bed.bed_number}:</span>
                  <span className={`status-text ${bed.status}`}>
                    {bed.status === 'occupied' 
                      ? `OCCUPIED by ${bed.student_name || 'Student'}` 
                      : 'AVAILABLE'}
                  </span>
                </div>
              ))}
          </div>
        </div>

        <div className="bed-legend">
          <div className="legend-item">
            <div className="legend-color available"></div>
            <span>Available ({roomDetails.beds.filter(bed => bed.status === 'available').length})</span>
          </div>
          <div className="legend-item">
            <div className="legend-color occupied"></div>
            <span>Occupied ({roomDetails.beds.filter(bed => bed.status === 'occupied').length})</span>
          </div>
          {!isViewOnly && (
            <div className="legend-item">
              <div className="legend-color selected"></div>
              <span>Selected</span>
            </div>
          )}
        </div>
      </div>
    );
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

              {/* Visual Room Selection */}
              <div className="visual-room-selection">
                <h3>All Rooms Overview</h3>
                <p className="selection-hint">Click on any room to view its bed layout. You can only select beds in available rooms.</p>
                
                {/* Room Filter Controls */}
                <div className="room-filters">
                  <button 
                    className={`filter-btn ${activeFilter === 'all' ? 'active' : ''}`}
                    onClick={() => setActiveFilter('all')}
                  >
                    All Rooms ({rooms.length})
                  </button>
                  <button 
                    className={`filter-btn ${activeFilter === 'available' ? 'active' : ''}`}
                    onClick={() => setActiveFilter('available')}
                  >
                    Available ({rooms.filter(r => r.available_beds > 0).length})
                  </button>
                  <button 
                    className={`filter-btn ${activeFilter === 'occupied' ? 'active' : ''}`}
                    onClick={() => setActiveFilter('occupied')}
                  >
                    Occupied ({rooms.filter(r => r.available_beds === 0).length})
                  </button>
                </div>

                <div className="rooms-grid">
                  {rooms
                    .filter(room => {
                      if (activeFilter === 'available') return room.available_beds > 0;
                      if (activeFilter === 'occupied') return room.available_beds === 0;
                      return true; // 'all'
                    })
                    .sort((a, b) => a.room_number.localeCompare(b.room_number, undefined, { numeric: true }))
                    .map(room => (
                      <RoomCard key={room.id} room={room} />
                    ))}
                </div>
                
                {rooms.filter(room => {
                  if (activeFilter === 'available') return room.available_beds > 0;
                  if (activeFilter === 'occupied') return room.available_beds === 0;
                  return true;
                }).length === 0 && (
                  <div className="no-rooms-message">
                    <p>No rooms found for the selected filter.</p>
                  </div>
                )}
              </div>

              {/* Bed Layout Modal */}
              {showBedLayout && selectedRoomDetails && (
                <div className="bed-layout-overlay">
                  <BedLayout roomDetails={selectedRoomDetails} />
                </div>
              )}

              {/* Form for reason input */}
              <form onSubmit={handleRoomChangeRequest} className="request-form">
                {roomChangeRequest.requested_room_id && roomChangeRequest.requested_bed_number && (
                  <div className="selection-summary">
                    <h4>Selected Room & Bed</h4>
                    <p>
                      Room {selectedRoomDetails?.room_number} - Floor {selectedRoomDetails?.floor} | 
                      Bed {roomChangeRequest.requested_bed_number}
                    </p>
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
                  disabled={!roomChangeRequest.requested_room_id || !roomChangeRequest.requested_bed_number}
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