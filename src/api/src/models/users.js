const users = []
const DEFAULT_LABELS = ["inbox", "starred",  "important", "sent", "drafts", "spam", "trash", "all"];

// Simple UUID v4-like generator
function IdGenerator() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'
    .replace(/[xy]/g, c => {
        const r = Math.random() * 16 | 0;          // random 0-f
        const v = c === 'x' ? r : (r & 0x3 | 0x8); // variant bits
        return v.toString(16);
    });
}

const getAllUsers = () => users;

const getUserById = (id) => users.find(user => user.id === id);

const createUser = (first_name, last_name, gender, birth_date, userName, email, password, profilePicture) => {
    const Label = require('./labels')
    const id = IdGenerator();
    const labels = DEFAULT_LABELS.map(labelName => (Label.createFirstLabel(labelName, id)));

    const user = {
        id: id,
        first_name,
        last_name,
        name: first_name + " " + last_name,
        gender: gender ?? null,
        birth_date: birth_date ?? null,
        userName,
        email: email.toLowerCase(),
        password,
        profilePicture: profilePicture ?? null,
        labels: labels,
    };

    user.toJSON = function () {
        const { password, ...safe } = this;
        return safe;
    };

    users.push(user);
    return user;
}

const getUserByUserName = (userName) => users.find(user => user.userName === userName);

const getLabelsOfUser = (user) => {
    return user.labels;
}

module.exports = {
    getAllUsers,
    getUserById,
    createUser,
    getUserByUserName,
    getLabelsOfUser
};