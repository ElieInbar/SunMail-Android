// /routes/mails.js

const express = require('express');
const router = express.Router();
const controller = require('../controllers/mails');
const { getUserId } = require('../utils/userUtils')

router.route('/')
    .post(getUserId, controller.createMail)
    .get(getUserId, controller.getAllMails)

router.route('/:id/send')
    .post(getUserId, controller.sendMail)

router.route('/search/:query')
    .get(getUserId, controller.searchMails);

router.route('/:id/read/:labelName')
    .patch(getUserId, controller.setRead);

router.route('/:id')
    .get(getUserId, controller.getMailById)
    .delete(getUserId, controller.deleteMail)
    .patch(getUserId, controller.editMail)

router.route('/label/:labelName')
    .get(getUserId, controller.getAllMailsOfLabel)

module.exports = router;
