const Users = require('../services/users');
const path = require('path');
const fs = require('fs');

const NAME_RE = /^[A-Za-z\u0590-\u05FF](?:[A-Za-z\u0590-\u05FF \-]{0,58}[A-Za-z\u0590-\u05FF])$/u;
const USERNAME_RE = /^(?!\.)(?!.*\.\.)([a-z0-9.]{6,30})(?<!\.)$/;
const PWD_RE = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z0-9!@#$%^&*()_\-+=\[\]{};':"\\|,.<>/?]{8,100}$/;
const GENDER_ENUM = ['male', 'female'];

function isValidName(str) { return NAME_RE.test(str); }
function isValidUsername(str) { return USERNAME_RE.test(str); }
function isValidPassword(str) { return PWD_RE.test(str); }
const isValidGender = g =>
    typeof g === 'string' && GENDER_ENUM.includes(g.trim().toLowerCase());
function parseBirthDate(str) {
    const m = str.match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
    if (!m) return null;
    const [, mm, dd, yyyy] = m.map(Number);
    const date = new Date(Date.UTC(yyyy, mm - 1, dd));
    const valid = date.getUTCFullYear() === yyyy &&
        date.getUTCMonth() === mm - 1 &&
        date.getUTCDate() === dd &&
        date <= new Date();
    return valid ? date.toISOString().slice(0, 10) : null;
}

function deleteUploadedFile(file) {
  if (file) {
    const filePath = path.join(__dirname, '..', 'uploads', file.filename);
    fs.unlink(filePath, (err) => {
      if (err) console.error("⚠️ Failed to delete uploaded file:", err.message);
    });
  }
}

function rejectWithError(res, file, message) {
  deleteUploadedFile(file);
  return res.status(400).json({ error: message });
}

const getAllUsers = async (_, res) => {
    try {
        const users = await Users.getAllUsers();
        res.status(200).json(users);
    } catch (error) {
        res.status(500).json({ error: error.message || 'Failed to retrieve users' });
    }
}

const getUserById = async (req, res) => {
    try {
        const user = await Users.getUserById(req.params.id);
        if (!user)
            return res.status(404).json({ error: 'User not found' });
        res.status(200).json(user);
    } catch (error) {
        res.status(500).json({ error: error.message || 'Failed to retrieve user' });
    }
};

const createUser = async (req, res) => {
    const { first_name, last_name, gender, birthDate, userName, password, confirmPassword } = req.body;

    let profilePicture = null;
    if (req.file) {
        profilePicture = `/uploads/${req.file.filename}`;
    }

    try {
        if (!first_name) return rejectWithError(res, req.file, 'first name is required');
        if (!userName) return rejectWithError(res, req.file, 'user name is required');
        if (!birthDate) return rejectWithError(res, req.file, 'birth date is required');
        if (!gender) return rejectWithError(res, req.file, 'gender is required');
        if (!password) return rejectWithError(res, req.file, 'password is required');
        if (password !== confirmPassword) {
            return rejectWithError(res, req.file, 'Passwords do not match');
        }
        if (!isValidName(first_name))
            return rejectWithError(res, req.file, 'first name must contain only letters and be at least 2 characters long');

        if (last_name && !isValidName(last_name))
            return rejectWithError(res, req.file, 'last name must contain only letters and be at least 2 characters long');

        const isoBirth = parseBirthDate(birthDate);
        if (!isoBirth)
            return rejectWithError(res, req.file, 'Birth Date must be a valid past date');

        if (!isValidGender(gender))
            return rejectWithError(res, req.file, 'Gender must be male or female');

        if (!isValidUsername(userName))
            return rejectWithError(res, req.file, 'user name must be 6-30 characters long and contain only letters or digits or periods only, with no leading/trailing/double dot');

        if (!isValidPassword(password))
            return rejectWithError(res, req.file, 'Password must be 8-100 chars, contain at least one letter and one digit, and may include common symbols');

        const userNameLower = userName.toLowerCase().trim();
        const email = `${userNameLower}@sunmail.com`;
        const exists = await Users.getUserByUserName(userNameLower);
        if (exists)
            return rejectWithError(res, req.file, 'Username already exists');

        const newUser = await Users.createUser(first_name, last_name, gender, birthDate, userNameLower, email, password, profilePicture);
        res.status(201).location(`/api/users/${newUser.id}`).end();

    } catch (error) {
        if (req.file)
            fs.unlink(req.file.path, ()=> {});
        res.status(500).json({ error: error.message || 'Registration failed' });
    }
};

const getUserByUserName = async (req, res) => {
    try {
        const user = await Users.getUserByUserName(req.params.userName);
        if (!user) return res.status(404).json({ error: 'User not found' });
        res.status(200).json(user);
    } catch (error) {
        res.status(500).json({ error: error.message || 'Failed to retrieve user' });
    }
};

module.exports = {
    getAllUsers,
    getUserById,
    createUser,
    getUserByUserName
};  