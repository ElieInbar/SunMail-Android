const Users = require('../models/users');
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


exports.getAllUsers = (req, res) => {
    res.status(200).json(Users.getAllUsers());
}

exports.getUserById = (req, res) => {
    const user = Users.getUserById(req.params.id);
    if (!user)
        return res.status(404).json({ error: 'User not found' });
    res.status(200).json(user);
};

exports.createUser = (req, res) => {
    const { first_name, last_name, gender, birthDate, userName, password, confirmPassword } = req.body;

    let profilePicture = null;
    if (req.file) {
        profilePicture = `/uploads/${req.file.filename}`;
    }

    try {
        if (!first_name) return res.status(400).json({ error: 'first name is required' });
        if (!userName) return res.status(400).json({ error: 'user name is required' });
        if (!birthDate) return res.status(400).json({ error: 'birth date is required' });
        if (!gender) return res.status(400).json({ error: 'gender is required' });
        if (!password) return res.status(400).json({ error: 'password is required' });
        if (password !== confirmPassword) {
            return res.status(400).json({ error: 'Passwords do not match' });
        }
        if (!isValidName(first_name))
            return res.status(400).json({ error: 'first name must contain only letters and be at least 2 characters long' });

        if (last_name && !isValidName(last_name))
            return res.status(400).json({ error: 'last name must contain only letters and be at least 2 characters long' });

        const isoBirth = parseBirthDate(birthDate);
        if (!isoBirth)
            return res.status(400).json({ error: 'Birth Date must be a valid past date' });

        if (!isValidGender(gender))
            return res.status(400).json({ error: 'Gender must be male or female' });

        if (!isValidUsername(userName))
            return res.status(400).json({ error: 'user name must be 6-30 characters long and contain only letters or digits or periods only, with no leading/trailing/double dot' });

        if (!isValidPassword(password))
            return res.status(400).json({ error: 'Password must be 8-100 chars, contain at least one letter and one digit, and may include common symbols' });

        const email = `${userName}@sunmail.com`;
        const exists = Users.getAllUsers().some(u => u.email.toLowerCase() === email.toLowerCase());
        if (exists)
            return res.status(400).json({ error: 'Username already exists' });

        const newUser = Users.createUser(first_name, last_name, gender, birthDate, userName, email, password, profilePicture);
        res.status(201).location(`/api/users/${newUser.id}`).end();

    } catch (error) {
        if (req.file) {
            fs.unlink(req.file.path, (err) => {
                if (err) console.error('Error deleting uploaded file:', err);
            });
        }
        res.status(500).json({ error: error.message || 'Registration failed' });
    }
};

exports.getUserByUserName = (req, res) => {
    const user = Users.getUserByUserName(req.params.userName);
    if (!user) return res.status(404).json({ error: 'User not found' });
    // Remove password before sending
    const { password, ...safe } = user;
    res.status(200).json(safe);
};