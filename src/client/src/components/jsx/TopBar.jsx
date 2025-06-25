import { useState, useContext, useEffect } from 'react';
import '../css/TopBar.css';
import { useNavigate } from 'react-router-dom';
import sunMailImg from '../../assets/sunmail_logo.png';
import { AuthContext } from '../../contexts/AuthContext';
import { useTheme } from '../../contexts/ThemeContext';
import UserDetailsPopup from './UserDetailsPopup';
import { MailContext } from "../../contexts/MailContext";


export default function TopBar({ toggleSidebar }) {
  const navigate = useNavigate();
  const { logout, isLoggedIn, username, userChecked } = useContext(AuthContext);
  const { setCurrentFolder } = useContext(MailContext)
  const [query, setQuery] = useState('');
  const [userInfo, setUserInfo] = useState(null);
  const [showUserDetails, setShowUserDetails] = useState(false);

  /* --- Dark-mode state persists in localStorage --- */
  const { darkMode, toggleTheme } = useTheme();


  useEffect(() => {
    if (userChecked && isLoggedIn && username) {
      fetch(`/api/users/by-username/${username}`)
        .then(res => {
          if (!res.ok) throw new Error('User not found');
          return res.json();
        })
        .then(data => setUserInfo(data))
        .catch(err => {
          console.warn('⚠️ Failed to fetch user info. Logging out.');
          logout();
          navigate('/login');
        });
    }
  }, [userChecked, isLoggedIn, username, logout, navigate]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    const trimmed = query.trim();
    if (trimmed) {
      navigate(`/search?q=${encodeURIComponent(trimmed)}`);
      setQuery('');
    }
  };

  return (
    <>
      {/* {darkMode && <div className="sunset-scene"></div>} */}
      <header className="topbar surface">
        {/* 1️⃣ Menu button */}
        <div className="topbar-left">
          <button className="menu-btn" onClick={toggleSidebar}>
            <i className="bi bi-list"></i>
          </button>

          {/* 2️⃣ Logo */}
          <button className="topbar-logo" onClick={() => {
            navigate('/inbox')
            setCurrentFolder("inbox");
          }}>
            <img src={sunMailImg
            } alt="Gmail" className="logo-image" />
          </button>
        </div>

        {/* 3️⃣ Search bar */}
        <form className="topbar-search" onSubmit={handleSearchSubmit}>
          <button type="submit" className="search-btn">
            <i className="bi bi-search"></i>
          </button>
          <input
            type="text"
            className="search-input"
            placeholder="Search messages"
            value={query}
            onChange={e => setQuery(e.target.value)}
          />
        </form>

        {/* 4️⃣ Theme toggle  */}
        <div className="topbar-right">
          <button
            className="theme-btn btn-ripple"
            onClick={toggleTheme}
            title={darkMode ? 'Switch to light mode' : 'Switch to dark mode'}
          >
            {darkMode ? <i className="bi bi-brightness-high"></i> : <i className="bi bi-moon"></i>}
          </button>

          {/* 5️⃣ User info */}
          {isLoggedIn && userInfo && (
            <div
              className="topbar-userinfo-container"
              onMouseEnter={() => setShowUserDetails(true)}
              onMouseLeave={() => setShowUserDetails(false)}
            >
              <div className="topbar-userinfo">
                {userInfo.profilePicture ? (
                  <img
                    src={userInfo.profilePicture}
                    alt="profile"
                    className="topbar-userinfo-img"
                  />
                ) : (
                  <div className="topbar-userinfo-fallback">
                    {userInfo.first_name ? userInfo.first_name[0].toUpperCase() : '?'}
                  </div>
                )}

              </div>
              {showUserDetails && <UserDetailsPopup user={userInfo} />}
            </div>
          )}

          {/* 6️⃣ Logout */}
          {isLoggedIn && (
            <button className="logout-btn" onClick={logout}>Logout</button>
          )}
        </div>
      </header>
    </>
  );
}
