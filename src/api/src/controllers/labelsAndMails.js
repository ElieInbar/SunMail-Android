const mailService = require('../services/mails');
const labelService = require('../services/labels');
const blacklistController = require('./blacklist');
const { validateUrls, extractUrls } = require('../utils/urlUtils');
const Mail = require('../models/mails');
const Label = require('../models/labels');

//get the labelId in the body, the mailId in the params
exports.addLabelToMail = async (req, res) => {
    try {
        const userId = req.id;
        if (!userId) return res.status(401).json({ error: 'User not authenticated' });

        const { labelId } = req.body;
        const mailId = req.params.mailId;

        if (!labelId) return res.status(400).json({ error: 'Label ID is required' });
        if (!mailId) return res.status(400).json({ error: 'Mail ID is required' });

        // Get the label
        const labelToAdd = await labelService.getLabelById(labelId, userId);
        if (!labelToAdd) return res.status(404).json({ error: 'Label not found' });

        // Get the mail
        const mail = await mailService.getMailById(userId, mailId);
        if (!mail) return res.status(404).json({ error: 'Mail not found' });

        // Check if mail already has this label
        const hasLabel = mail.labels.some(label => label._id.toString() === labelToAdd._id.toString());
        if (hasLabel) {
            return res.status(400).json({ error: 'Mail already belongs to this label.' });
        }

        // Special handling for spam label
        if (labelToAdd.name === "spam") {
            // Remove all user labels from mail
            const otherLabels = mail.labels.filter(label => label.userId !== userId);
            mail.labels = otherLabels;

            // Add URLs to blacklist
            const urls = extractUrls(`${mail.subject} ${mail.body}`);
            for (const url of urls) {
                req.body = { url: url };
                await blacklistController.createBlacklistEntry(req, res);
            }
        }

        // Add the label to the mail
        mail.labels.push(labelToAdd._id);
        await mail.save();

        return res.status(201).json({ message: 'Label added to mail successfully' });
    } catch (error) {
        console.error('Error adding label to mail:', error);
        return res.status(500).json({ error: 'Internal server error' });
    }
};

//get the mailId and the labelId in the params
exports.deleteLabelFromMail = async (req, res) => {
    try {
        const userId = req.id;
        if (!userId) return res.status(401).json({ error: 'User not authenticated' });

        const { labelId, mailId } = req.params;

        if (!labelId) return res.status(400).json({ error: 'Label ID is required' });
        if (!mailId) return res.status(400).json({ error: 'Mail ID is required' });

        // Get the label
        const labelToRemove = await labelService.getLabelById(labelId, userId);
        if (!labelToRemove) return res.status(404).json({ error: 'Label not found' });

        // Get the mail
        const mail = await mailService.getMailById(userId, mailId);
        if (!mail) return res.status(404).json({ error: 'Mail not found' });

        // Check if mail has this label
        const hasLabel = mail.labels.some(label => label._id.toString() === labelToRemove._id.toString());
        if (!hasLabel) {
            return res.status(404).json({ error: 'The mail does not belong to this label' });
        }

        // Special handling for spam label removal
        if (labelToRemove.name === "spam") {
            const urls = extractUrls(`${mail.subject} ${mail.body}`);
            for (const url of urls) {
                req.body = { url: url };
                await blacklistController.deleteBlacklistEntry(req, res);
            }
        }

        // Remove the label from the mail
        mail.labels = mail.labels.filter(label => label._id.toString() !== labelToRemove._id.toString());
        await mail.save();

        return res.status(204).end();
    } catch (error) {
        console.error('Error removing label from mail:', error);
        return res.status(500).json({ error: 'Internal server error' });
    }
};


exports.getLabelFromMailById = async (req, res) => {
    try {
        const userId = req.id;
        if (!userId) return res.status(401).json({ error: 'User not authenticated' });

        const { labelId, mailId } = req.params;

        if (!labelId) return res.status(400).json({ error: 'Label ID is required' });
        if (!mailId) return res.status(400).json({ error: 'Mail ID is required' });

        // Get the label
        const labelToGet = await labelService.getLabelById(labelId, userId);
        if (!labelToGet) return res.status(404).json({ error: 'Label not found' });

        // Get the mail
        const mail = await mailService.getMailById(userId, mailId);
        if (!mail) return res.status(404).json({ error: 'Mail not found' });

        // Check if mail has this label
        const hasLabel = mail.labels.some(label => label._id.toString() === labelToGet._id.toString());
        if (hasLabel) {
            return res.status(200).json(labelToGet);
        } else {
            return res.status(404).json({ error: 'Label not found in this mail' });
        }
    } catch (error) {
        console.error('Error getting label from mail:', error);
        return res.status(500).json({ error: 'Internal server error' });
    }
};
