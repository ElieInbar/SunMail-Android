// models/blacklist.js

const mongoose = require('mongoose');

const blacklistSchema = new mongoose.Schema({
  url: {
    type: String,
    required: true,
    unique: true
  }
}, {
  timestamps: true
});

module.exports = mongoose.model('Blacklist', blacklistSchema);
