
const jwt = require('jsonwebtoken')
exports.getUserId = (req, res, next) => {
    const token = req.cookies.token;
    if (!token) {
        res.status(404).json({ error: 'Token not found' }).end()
        return;
    }
    try {
        const decoded = jwt.verify(token, process.env.SECRET);
        req.id = decoded.id
        next();
    } catch (err) {
        return res.status(404).json({ error: 'Invalid or expired token' });
    }
};

