const express = require('express');
const router = express.Router();
const controller = require('../controllers/labelsAndMails');
const { getUserId } = require('../utils/userUtils')

//route to add label to mail
router.route('/:mailId')
    .post(getUserId, controller.addLabelToMail);

router.route('/:mailId/:labelId')
    .delete(getUserId, controller.deleteLabelFromMail)
    .get(getUserId, controller.getLabelFromMailById);

module.exports = router;