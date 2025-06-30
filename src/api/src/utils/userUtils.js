
const jwt = require('jsonwebtoken')
exports.getUserId = (req, res, next) => {
    const token = req.cookies.token;
    if (!token) {
        res.status(401).json({ error: 'Authentication required - please login' });
        return;
    }
    try {
        const decoded = jwt.verify(token, process.env.SECRET);
        req.id = decoded.id;
        next();
    } catch (err) {
        return res.status(401).json({ error: 'Invalid or expired token - please login again' });
    }
};
