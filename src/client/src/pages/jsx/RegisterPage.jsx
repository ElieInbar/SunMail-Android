// src/client/src/pages/jsx/RegisterPage.jsx
import React, { useState, useContext } from "react";
import { useNavigate, Link } from "react-router-dom";
import { AuthContext } from "../../contexts/AuthContext";
import sunMailImg from '../../assets/sunmail_logo.png';
import "../../styles/auth.css";

export default function RegisterPage() {
  const [form, setForm] = useState({
    first_name: '',
    last_name: '',
    gender: '',
    birthDate: '',
    userName: '',
    password: '',
    confirmPassword: '',
    profilePicture: ''
  });
  const [imagePreview, setImagePreview] = useState("");
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setForm({ ...form, profilePicture: file });
      const reader = new FileReader();
      reader.onloadend = () => setImagePreview(reader.result);
      reader.readAsDataURL(file);
    }
  };

  const handleDeleteImage = () => {
    setForm({ ...form, profilePicture: null });
    setImagePreview("");
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);

    try {
      const formData = new FormData();
      formData.append('first_name', form.first_name);
      formData.append('last_name', form.last_name);
      formData.append('gender', form.gender);

      // Format the date from YYYY-MM-DD to MM/DD/YYYY
      const [year, month, day] = form.birthDate.split('-');
      const formattedDate = `${month}/${day}/${year}`;
      formData.append('birthDate', formattedDate);

      formData.append('userName', form.userName);
      formData.append('password', form.password);
      formData.append('confirmPassword', form.confirmPassword);
      if (form.profilePicture) {
        formData.append('profilePicture', form.profilePicture);
      }

      // Change this line to point to the correct port
      const res = await fetch('/api/users', {
        method: 'POST',
        credentials: 'include', // Add this for cookies
        body: formData,
      });

      if (res.status === 201) {
        const email = `${form.userName}@sunmail.com`;
        const loginRes = await fetch("/api/tokens", {
          method: "POST",
          credentials: "include",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ email, password: form.password }),
        });
        if (loginRes.status === 201) {
          login(true, form.userName);
          navigate("/inbox");
        } else {
          setError("Registration succeeded but login failed.");
        }
      } else {
        const err = await res.json();
        setError(err.error || 'Registration failed');
      }
    } catch (err) {
      if (err.message && err.message.includes('Invalid file type')) {
        setError(err.message);
      } else {
        setError("Network error. Please try again.");
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="auth-bg">
      <div className="auth-card">
        <div className="auth-logo-wrapper">
          <img src={sunMailImg} alt="SunMail logo" className="auth-logo" />
        </div>
        <div className="auth-title">Create your account</div>
        <form className="auth-form" onSubmit={handleRegister}>
          <input
            type="text"
            name="first_name"
            placeholder="First Name"
            value={form.first_name}
            onChange={handleChange}
            required
          />
          <input
            type="text"
            name="last_name"
            placeholder="Last Name"
            value={form.last_name}
            onChange={handleChange}
          />
          <div className="select-wrapper">
            <select
              name="gender"
              value={form.gender}
              onChange={handleChange}
              required
            >
              <option value="">Gender</option>
              <option value="male">Male</option>
              <option value="female">Female</option>
            </select>
          </div>
          <input type="date" name="birthDate" value={form.birthDate} onChange={handleChange} required className="birthdate-input" />
          <input type="text" name="userName" placeholder="Username" value={form.userName} onChange={handleChange} required />
          <input type="password" name="password" placeholder="Password" value={form.password} onChange={handleChange} required />
          <input type="password" name="confirmPassword" placeholder="Confirm Password" value={form.confirmPassword} onChange={handleChange} required />
          <input type="file" id="profilePictureInput" accept="image/*" onChange={handleImageChange} style={{ display: 'none' }} />
          {imagePreview ? (
            <div className="auth-profile-preview">
              <img src={imagePreview} alt="Preview" className="auth-profile-img" />
              <div className="image-upload-controls">
                <button type="button" onClick={handleDeleteImage} className="delete-image-btn">
                  Remove Image
                </button>
                <button
                  type="button"
                  className="change-image-btn"
                  onClick={() => document.getElementById('profilePictureInput').click()}
                >
                  Change Image
                </button>

              </div>
            </div>
          ) : (
            <label htmlFor="profilePictureInput" className="upload-image-btn">
              Upload Profile Picture
            </label>
          )}
          {error && <div className="auth-error">{error}</div>}
          <button className="auth-btn" type="submit" disabled={isLoading}>
            {isLoading ? "Loading..." : "Register"}
          </button>
        </form>
        <div className="auth-link">
          Already have an account? <Link to="/login">Login here</Link>
        </div>
      </div>
    </div>
  );
}
