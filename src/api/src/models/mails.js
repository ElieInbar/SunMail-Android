// models/mails.js

const mongoose = require('mongoose');

const mailSchema = new mongoose.Schema({
  id: {
    type: String,
    required: true,
    unique: true
  },
  from: {
    type: String,
    required: true,
    ref: 'User'
  },
  to: {
    type: String,
    required: false, // Allow empty for drafts
    default: '',
    ref: 'User'
  },
  subject: {
    type: String,
    required: false, // Allow empty for drafts
    default: ''
  },
  body: {
    type: String,
    required: false, // Allow empty for drafts
    default: ''
  },
  labels: [{
    type: mongoose.Schema.Types.ObjectId,
    ref: 'Label'
  }],
  read: {
    type: Boolean,
    default: false
  },
  date: {
    type: Date,
    default: Date.now
  }
}, {
  timestamps: true
});

// Index pour optimiser les requêtes
mailSchema.index({ from: 1, date: -1 });
mailSchema.index({ to: 1, date: -1 });
mailSchema.index({ labels: 1 });

module.exports = mongoose.model('Mail', mailSchema);
