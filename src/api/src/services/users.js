const User = require('../models/users');
const labelService = require('./labels');
const DEFAULT_LABELS = ["inbox", "starred", "important", "sent", "drafts", "spam", "trash", "all"];
const populate = q => q.populate('labels');

// Simple UUID v4-like generator
function IdGenerator() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'
        .replace(/[xy]/g, c => {
            const r = Math.random() * 16 | 0;          // random 0-f
            const v = c === 'x' ? r : (r & 0x3 | 0x8); // variant bits
            return v.toString(16);
        });
}

const getAllUsers = async() => {
    try {
        return await User.find({});
    } catch (error) {
        console.error('Error fetching all users:', error);
        throw error;
    }
};

const getUserById = async(id) => {
    try {
        return await User.findOne({ id });
    } catch (error) {
        console.error('Error fetching user by ID:', error);
        throw error;
    }
};

const createUser = async (first_name, last_name, gender, birthDate, userName, email, password, profilePicture) => {
    try {
        const id = IdGenerator();
        const createdLabels = await Promise.all(DEFAULT_LABELS.map( labelName => labelService.createLabel(labelName, id)));
        const labelIds = createdLabels.map(label => label._id);

        const user = new User({
            id,
            first_name,
            last_name,
            name: `${first_name} ${last_name || ''}`.trim(),
            gender,
            birthDate,
            userName : userName.toLowerCase(),
            email: email.toLowerCase(),
            password,
            profilePicture,
            labels: labelIds,
        });

        await user.save();
        return user.populate('labels'); // Populate labels after saving
    } catch (error) {
        console.error('Error creating user:', error);
        throw error;
    }
}

const getUserByUserName = async (userName) => {
    try {
        return await User.findOne({ userName });
    } catch (error) {
        console.error('Error fetching user by username:', error);
        throw error;
    }
};

const getLabelsOfUser = async (user) => {
    try {
        await user.populate('labels');
        return user.labels;
    } catch (error) {
        console.error('Error fetching labels of user:', error);
        throw error;
    }
}

module.exports = {
    getAllUsers,
    getUserById,
    createUser,
    getUserByUserName,
    getLabelsOfUser
};