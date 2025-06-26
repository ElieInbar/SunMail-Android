const tokens = require('../models/tokens')

exports.login = async(req, res) => {
    const { email, password } = req.body;
    if (!email) {
        return res.status(400).json({ error: 'Email is required' })
    }
    if (!password) {
        return res.status(400).json({ error: 'Password is required' })
    }
    const jwtRes =  await tokens.login(email, password);
    if (jwtRes === -1) {
        return res.status(400).json({ error: 'Email or Password incorrect' })
    }
    res.cookie('token', jwtRes.token, {
        httpOnly: true,    // Makes cookie inaccessible to JavaScript (security)
        secure: false, // HTTPS only in production
        sameSite: 'lax', // CSRF protection
        maxAge: 24 * 60 * 60 * 1000 // 24 hours in milliseconds
    });
    res.status(201).end()
}
