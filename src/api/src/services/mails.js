// services/mails.js

const Mail = require('../models/mails');
const User = require('../models/users');
const Label = require('../models/labels');
const { validateUrls } = require('../utils/urlUtils');

// Simple UUID v4-like generator
function generateMailId() {
    return `mail_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
}

/**
 * Get the last 50 mails for a user with a specific label
 */
const getLast50 = async (userId, labelName) => {
    try {
        const label = await Label.findOne({ name: labelName, userId });
        if (!label) return [];

        const mails = await Mail.find({
            $or: [{ from: userId }, { to: userId }],
            labels: label._id
        })
        .populate('labels')
        .sort({ date: -1 })
        .limit(50);

        return mails;
    } catch (error) {
        console.error('Error fetching last 50 mails:', error);
        throw error;
    }
};

/**
 * Get a mail by ID for a specific user and label
 */
const getById = async (userId, mailId, labelName) => {
    try {
        const label = await Label.findOne({ name: labelName, userId });
        if (!label) return null;

        const mail = await Mail.findOne({
            id: mailId,
            $or: [{ from: userId }, { to: userId }],
            labels: label._id
        }).populate('labels');

        return mail;
    } catch (error) {
        console.error('Error fetching mail by ID:', error);
        throw error;
    }
};

/**
 * Get a mail by ID for a specific user (any label)
 */
const getMailById = async (userId, mailId) => {
    try {
        const mail = await Mail.findOne({
            id: mailId,
            $or: [{ from: userId }, { to: userId }]
        }).populate('labels');

        return mail;
    } catch (error) {
        console.error('Error fetching mail by ID:', error);
        throw error;
    }
};

/**
 * Create a new mail (draft)
 */
const create = async (fromUserId, toUserId = '', subject = '', body = '') => {
    try {
        const fromUser = await User.findOne({ id: fromUserId });
        if (!fromUser) return null;

        // Get drafts label
        const draftsLabel = await Label.findOne({ name: "drafts", userId: fromUserId });
        if (!draftsLabel) throw new Error('Drafts label not found');

        const mail = new Mail({
            id: generateMailId(),
            from: fromUserId,
            to: toUserId || '', // Allow empty recipient for draft
            subject: subject || '', // Allow empty subject for draft
            body: body || '', // Allow empty body for draft
            labels: [draftsLabel._id],
            read: false
        });

        await mail.save();
        return await mail.populate('labels');
    } catch (error) {
        console.error('Error creating mail:', error);
        throw error;
    }
};

/**
 * Search mails by subject or body
 */
const search = async (userId, query) => {
    try {
        const draftLabel = await Label.findOne({ name: "drafts", userId });

        const mails = await Mail.find({
            $or: [{ from: userId }, { to: userId }],
            $and: [
                {
                    $or: [
                        { subject: { $regex: query, $options: 'i' } },
                        { body: { $regex: query, $options: 'i' } }
                    ]
                },
                { labels: { $ne: draftLabel?._id } }
            ]
        }).populate('labels');

        return mails;
    } catch (error) {
        console.error('Error searching mails:', error);
        throw error;
    }
};

/**
 * Get all mails for a user
 */
const getAllMails = async (userId) => {
    try {
        const mails = await Mail.find({
            $or: [{ from: userId }, { to: userId }]
        }).populate('labels');

        return mails;
    } catch (error) {
        console.error('Error fetching all mails:', error);
        throw error;
    }
};

/**
 * Mark mail as read
 */
const setRead = async (userId, mailId, labelName) => {
    try {
        const mail = await getById(userId, mailId, labelName);
        if (!mail) return null;

        mail.read = true;
        await mail.save();
        return mail;
    } catch (error) {
        console.error('Error setting mail as read:', error);
        throw error;
    }
};

/**
 * Delete mail or move to trash
 */
const deleteMail = async (userId, mailId) => {
    try {
        const mail = await Mail.findOne({
            id: mailId,
            $or: [{ from: userId }, { to: userId }]
        }).populate('labels');

        if (!mail) return false;

        const trashLabel = await Label.findOne({ name: "trash", userId });
        const allLabel = await Label.findOne({ name: "all", userId });

        // Check if already in trash
        const isInTrash = mail.labels.some(label =>
            label.userId === userId && label.name === "trash"
        );

        if (isInTrash) {
            // Permanently delete
            await Mail.deleteOne({ _id: mail._id });
            return true;
        } else {
            // Move to trash - remove user's labels and add trash
            const userLabels = mail.labels.filter(label => label.userId === userId);
            const otherLabels = mail.labels.filter(label => label.userId !== userId);

            mail.labels = [...otherLabels];
            if (trashLabel) mail.labels.push(trashLabel._id);
            if (allLabel) mail.labels.push(allLabel._id);

            await mail.save();
            return true;
        }
    } catch (error) {
        console.error('Error deleting mail:', error);
        throw error;
    }
};

/**
 * Edit mail (for drafts)
 */
const editMail = async (userId, mailId, to, subject, body) => {
    try {
        const toUser = await User.findOne({ userName: to.split("@")[0] });
        const mail = await getMailById(userId, mailId);

        if (!mail) return null;

        if (toUser) mail.to = toUser.id;
        if (subject) mail.subject = subject;
        if (body) mail.body = body;
        mail.date = new Date();

        await mail.save();
        return mail;
    } catch (error) {
        console.error('Error editing mail:', error);
        throw error;
    }
};

/**
 * Send mail (move from drafts to sent/inbox)
 */
const sendMail = async (userId, mailId) => {
    try {
        const mail = await getMailById(userId, mailId);
        if (!mail) return null;

        const fromUser = await User.findOne({ id: mail.from });
        const toUser = await User.findOne({ id: mail.to });

        if (!fromUser || !toUser) return null;

        // Remove all user labels from mail
        const otherLabels = mail.labels.filter(label => label.userId !== userId);
        mail.labels = otherLabels;

        // Check for spam
        const isSpam = await validateUrls(mail.subject, mail.body);

        if (isSpam) {
            // Add to recipient's spam
            const spamLabel = await Label.findOne({ name: "spam", userId: toUser.id });
            if (spamLabel) mail.labels.push(spamLabel._id);
        } else {
            // Add to recipient's inbox
            const inboxLabel = await Label.findOne({ name: "inbox", userId: toUser.id });
            const allLabel = await Label.findOne({ name: "all", userId: toUser.id });
            if (inboxLabel) mail.labels.push(inboxLabel._id);
            if (allLabel) mail.labels.push(allLabel._id);
        }

        // Add to sender's sent items
        const sentLabel = await Label.findOne({ name: "sent", userId: fromUser.id });
        if (sentLabel) mail.labels.push(sentLabel._id);

        await mail.save();

        return {
            ...mail.toObject(),
            fromEmail: fromUser.email,
            toEmail: toUser.email
        };
    } catch (error) {
        console.error('Error sending mail:', error);
        throw error;
    }
};

module.exports = {
    getLast50,
    getById,
    getMailById,
    create,
    search,
    getAllMails,
    setRead,
    deleteMail,
    editMail,
    sendMail
};
