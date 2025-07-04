const Label = require('../models/labels');
const DEFAULT_LABELS = ["starred", "important", "sent", "drafts", "trash", "all"];

//function that generates IDs
function IdGenerator() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        const r = Math.random() * 16 | 0;
        const v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

const createLabel = async (name, userId) => {
    const label = new Label({ id: IdGenerator(), name, userId });
    return await label.save();
}

const getLabelById = async (id, userId) => {
    return await Label.findOne({ id, userId });
}

const getLabels = async (userId) => {
    return await Label.find({
        userId,
        name: { $nin: DEFAULT_LABELS }
    });
};

const patchLabelById = async (id, name, userId) => {
    const label = await getLabelById(id, userId);
    if (!label) return null;

    label.name = name;
    await label.save();
    return label;
}

const deleteLabelById = async (id, userId) => {
    const label = await getLabelById(id, userId);
    if (!label) return null;

    await label.deleteOne();
    return label;
}

const getLabelByName = async (name, userId) => {
    return await Label.findOne({ name, userId });
}

/**
 * Get all mails that have a specific label
 */
const getLabelMails = async (labelId, userId) => {
    try {
        const Mail = require('../models/mails');

        const label = await Label.findOne({ id: labelId, userId });
        if (!label) return null;

        const mails = await Mail.find({
            $or: [{ from: userId }, { to: userId }],
            labels: label._id
        })
        .populate('labels')
        .sort({ date: -1 });

        return mails;
    } catch (error) {
        console.error('Error getting label mails:', error);
        throw error;
    }
};

module.exports = { createLabel, getLabelById, getLabels, patchLabelById, deleteLabelById, getLabelByName, getLabelMails }
