// /routes/labels.js
const express = require('express')
var router = express.Router()
const { getUserId } = require('../utils/userUtils')

const labels = require('../controllers/labels')

router.route('/')
    .get(getUserId, labels.getLabels)
    .post(getUserId, labels.createLabel);

router.route('/name/:name')
    .get(getUserId, labels.getLabelByName)

router.route('/:id')
    .get(getUserId, labels.getLabelById)
    .patch(getUserId, labels.patchLabelById)
    .delete(getUserId, labels.deleteLabelById);


module.exports = router
