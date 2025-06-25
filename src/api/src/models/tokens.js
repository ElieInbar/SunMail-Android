const Users = require('./users')
const jwt = require("jsonwebtoken")

function login(email, password) {
    const users = Users.getAllUsers()
    const user = users.find(u => u.email === email)
    if (user && user.password === password) {
        const data = { id: user.id }
        const token = jwt.sign(data, process.env.SECRET);
        return { token }
    } else {
        return -1;
    }
}

module.exports = { login }

