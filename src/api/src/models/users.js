const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
    id: {
        type: String,
        required: true,
        unique: true
    },
    first_name: {
        type: String,
        required: true
    },
    last_name: {
        type: String,
        default: ''
    },
    name: {
        type: String
    },
    gender: {
        type: String,
        enum: ['male', 'female']
    },
    birthDate: {
        type: Date,
    },
    userName: {
        type: String,
        required: true,
        unique: true,
        lowercase: true
    },
    email: {
        type: String,
        required: true,
        unique: true,
        lowercase: true
    },
    password: {
        type: String,
        required: true
    },
    profilePicture: {
        type: String,
        default: null
    },
    labels: [{
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Label'
    }]
}, {
    timestamps: true,
    toJSON: {
        transform: function (doc, ret) {
            delete ret.password; // Exclude password from the JSON output
            delete ret.__v; // Exclude version key from the JSON output
            return ret;
        }
    }
});

module.exports = mongoose.model('User', userSchema);
