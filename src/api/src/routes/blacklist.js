// src/api/src/routes/blacklist.js

const express = require('express');
const router = express.Router();
const controller = require('../controllers/blacklist');
const { getUserId } = require('../utils/userUtils');

// GET /api/blacklist
router.get('/', getUserId, controller.getAllBlacklistEntries);

// POST /api/blacklist
router.post('/', getUserId, controller.createBlacklistEntry);

// DELETE /api/blacklist
router.delete('/', getUserId, controller.deleteBlacklistEntry);

module.exports = router;
