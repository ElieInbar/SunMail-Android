// routes/users.js
const express = require('express');
var router = express.Router();
const controller = require('../controllers/users');
const upload = require('../middleWare/uploadFile');

router.route('/')
    .get(controller.getAllUsers)
    .post(upload.single('profilePicture'), controller.createUser);

router.get('/by-username/:userName', controller.getUserByUserName);

router.route('/:id')
    .get(controller.getUserById);


module.exports = router;
