// const Users = require('../services/users')
// const jwt = require("jsonwebtoken")

// async function login(email, password) {
//     const users = (await Users.getAllUsers()).map(user => user.toJSON());

//     const user = users.find(u => u.email === email)
//     if (user && user.password === password) {
//         const data = { id: user.id }
//         const token = jwt.sign(data, process.env.SECRET);
//         return { token }
//     } else {
//         return -1;
//     }
// }

// module.exports = { login }

const Users = require('../services/users');
const jwt = require("jsonwebtoken");

async function login(email, password) {
    const users = await Users.getAllUsers(); // עדיין מסמכי Mongoose

    const user = users.find(u => u.email === email && u.password === password);
    if (user) {
        const data = { id: user.id };
        const token = jwt.sign(data, process.env.SECRET);

        // אפשר להחזיר גם את המשתמש בלי הסיסמה אם רוצים
        const userJson = user.toJSON(); // עכשיו הסיסמה כבר תימחק
        return { token, user: userJson };
    } else {
        return -1;
    }
}

module.exports = { login };