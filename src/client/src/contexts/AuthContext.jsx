// src/contexts/AuthContext.js
import React, { createContext, useState, useEffect } from 'react';

// Create the context
export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(() => localStorage.getItem('token'));
  const [username, setUsername] = useState(() => localStorage.getItem('username'));
  const [userChecked, setUserChecked] = useState(false);

  // Load from localStorage once on mount
  useEffect(() => {
    const storedToken = localStorage.getItem('token');
    const storedUsername = localStorage.getItem('username');

    if (storedToken) {
      setToken(storedToken);
      setUsername(storedUsername);
    }

    setUserChecked(true); // signal that user check is complete
  }, []);

  useEffect(() => {
    if (token) {
      localStorage.setItem('token', token);
      localStorage.setItem('username', username);
    } else {
      localStorage.removeItem('token');
      localStorage.removeItem('username');
    }
  }, [token, username]);

  const login = (newToken, newUsername) => {
    setToken(newToken);
    setUsername(newUsername);
  };

  const logout = () => {
    setToken(null);
    setUsername(null);
  };

  const isLoggedIn = !!token;

  return (
    <AuthContext.Provider value={{ token, username, isLoggedIn, login, logout, userChecked }}>
      {children}
    </AuthContext.Provider>
  );
};
