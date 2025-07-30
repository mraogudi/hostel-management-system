import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import './ChangePassword.css';

const ChangePassword = () => {
  const [passwords, setPasswords] = useState({
    current_password: '',
    new_password: '',
    confirm_password: ''
  });
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const { user, apiCall, fetchProfile } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    // If user is not on first login, redirect them based on their role
    if (user && !user.first_login) {
      navigate(user.role === 'warden' ? '/warden' : '/student');
    }
  }, [user, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    // Validate passwords match
    if (passwords.new_password !== passwords.confirm_password) {
      setError('New passwords do not match');
      setIsLoading(false);
      return;
    }

    // Validate password length
    if (passwords.new_password.length < 6) {
      setError('New password must be at least 6 characters long');
      setIsLoading(false);
      return;
    }

    const result = await apiCall('POST', '/api/change-password', {
      current_password: passwords.current_password,
      new_password: passwords.new_password
    });

    if (result.success) {
      // Refresh user data to get updated firstLogin status
      await fetchProfile();
      
      // Redirect to appropriate dashboard
      navigate(user.role === 'warden' ? '/warden' : '/student');
    } else {
      setError(result.error);
    }
    
    setIsLoading(false);
  };

  const handleChange = (e) => {
    setPasswords({
      ...passwords,
      [e.target.name]: e.target.value
    });
  };

  if (!user || !user.first_login) {
    return <div className="loading">Redirecting...</div>;
  }

  return (
    <div className="change-password-container">
      <div className="change-password-card">
        <div className="change-password-header">
          <h2>Change Your Password</h2>
          <p className="welcome-message">
            Welcome, {user.full_name}! For security reasons, you must change your password before accessing your dashboard.
          </p>
        </div>

        <form onSubmit={handleSubmit} className="change-password-form">
          {error && <div className="error-message">{error}</div>}

          <div className="form-group">
            <label htmlFor="current_password">Current Password *</label>
            <input
              type="password"
              id="current_password"
              name="current_password"
              value={passwords.current_password}
              onChange={handleChange}
              required
              placeholder="Enter your current password"
              autoFocus
            />
            <small className="field-note">Use the password provided to you by the warden</small>
          </div>

          <div className="form-group">
            <label htmlFor="new_password">New Password *</label>
            <input
              type="password"
              id="new_password"
              name="new_password"
              value={passwords.new_password}
              onChange={handleChange}
              required
              placeholder="Enter your new password (min. 6 characters)"
              minLength="6"
            />
          </div>

          <div className="form-group">
            <label htmlFor="confirm_password">Confirm New Password *</label>
            <input
              type="password"
              id="confirm_password"
              name="confirm_password"
              value={passwords.confirm_password}
              onChange={handleChange}
              required
              placeholder="Re-enter your new password"
              minLength="6"
            />
            {passwords.new_password && passwords.confirm_password && 
             passwords.new_password !== passwords.confirm_password && (
              <span className="validation-error">Passwords do not match</span>
            )}
            {passwords.new_password && passwords.confirm_password && 
             passwords.new_password === passwords.confirm_password && 
             passwords.new_password.length >= 6 && (
              <span className="validation-success">✓ Passwords match</span>
            )}
          </div>

          <div className="form-actions">
            <button 
              type="submit" 
              className="change-password-button"
              disabled={isLoading || !passwords.current_password || !passwords.new_password || 
                       !passwords.confirm_password || passwords.new_password !== passwords.confirm_password}
            >
              {isLoading ? 'Changing Password...' : 'Change Password'}
            </button>
          </div>

          <div className="security-info">
            <h4>Password Requirements:</h4>
            <ul>
              <li>Minimum 6 characters long</li>
              <li>Keep it secure and don't share with others</li>
              <li>Remember it for future logins</li>
            </ul>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ChangePassword; 