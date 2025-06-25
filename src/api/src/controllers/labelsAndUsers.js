const User = require('../models/users');
const { getLabelById } = require('../models/labels')
const labelsAndUsers = require('../models/labelsAndUsers')

exports.addLabelToUser = (req, res) => {
    const userId = req.id
    if (!userId) return;

    const { labelId } = req.body
    const labelToAdd = getLabelById(labelId, userId);
    if (!labelToAdd) return res.status(404).json({ error: 'Label not found' });

    if (User.getLabelsOfUser(userId).find(l => l == labelToAdd)) {
        return res.status(400).json({ error: 'User already have this label.' })
    }
    const returnedLabel = labelsAndUsers.addLabelToUser(userId, labelToAdd);

    if (returnedLabel == labelToAdd) {
        return res.status(201).end();
    } else {
        return res.status(404).json({ error: 'Label not added' }).end();
    }
}

exports.getLabelFromUserById = (req, res) => {
    const userId = req.id
    if (!userId) return;
    const { labelId } = req.body
    const labelToGet = getLabelById(labelId, userId);
    if (!labelToGet) return res.status(404).json({ error: 'Label not found' });

    const returnedLabel = labelsAndUsers.getLabelFromUserById(userId, labelToGet);
    if (returnedLabel == labelToGet) {
        return res.status(200).end();
    } else {
        return res.status(404).json({ error: 'Label not added' }).end();
    }
}

exports.deleteLabelFromUser = (req, res) => {
    const userId = req.id
    if (!userId) return;
    const { labelId } = req.body
    const labelToRemove = getLabelById(labelId, userId);
    if (!labelToRemove) return res.status(404).json({ error: 'Label not found' });

    if (!labelsAndUsers.getLabelFromUserById(userId, labelToRemove)) {
        return res.status(404).json({ error: 'The user does not have this label' });
    }

    const ret = labelsAndUsers.deleteLabelFromUser(userId, labelToRemove);

    if (ret == -1) {
        return res.status(404).json({ error: 'Label not removed' }).end();
    } else {
        return res.status(204).end();
    }
}