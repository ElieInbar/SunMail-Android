const express = require('express');
const router = express.Router();
const controller = require('../controllers/labelsAndUsers');
const { getUserId } = require('../utils/userUtils')

//route to add label to user
router.route('/:userId')
    .post(getUserId, controller.addLabelToUser);

router.route('/:userId/:labelId')
    .delete(getUserId, controller.deleteLabelFromUser)
    .get(getUserId, controller.getLabelFromUserById);

module.exports = router;