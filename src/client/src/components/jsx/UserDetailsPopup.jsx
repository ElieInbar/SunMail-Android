import React from 'react';
import '../css/UserDetailsPopup.css';

export default function UserDetailsPopup({ user }) {
  if (!user) return null;

  return (
    <div className="user-details-popup">
      <div className="popup-header">
        {user.profilePicture ? (
          <img src={user.profilePicture} alt="Profile" className="popup-profile-img" />
        ) : (
          <div className="popup-profile-fallback">
            {user.first_name ? user.first_name[0].toUpperCase() : '?'}
          </div>
        )}
        <div className="popup-user-info">
          <span className="popup-user-name">
            {user.first_name} {user.last_name}
          </span>
          <span className="popup-user-email">{user.email}</span>
        </div>
      </div>
    </div>
  );
} 