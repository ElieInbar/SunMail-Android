const Mail = require('../models/mails');
const { getLabelById } = require('../models/labels')
const labelsAndMails = require('../models/labelsAndMails')
const black = require('./blacklist')
const { validateUrls, extractUrls } = require('../utils/urlUtils');


//get the labelId in the body, the mailId in the params
exports.addLabelToMail = (req, res) => {
    const userId = req.id
    if (!userId) return;

    const { labelId } = req.body
    const labelToAdd = getLabelById(labelId, userId);
    if (!labelToAdd) return res.status(404).json({ error: 'Label not found' });
    const mail = Mail.getMailById(userId, req.params.mailId);
    if (!mail) return res.status(404).json({ error: 'Mail not found' });

    if (Mail.getLabelsOfMail(mail).find(l => l == labelToAdd)) {
        return res.status(400).json({ error: 'Mail already belongs to this label.' })
    }

    if (labelToAdd.name == "spam") {
        const labels = Mail.getLabelsOfMail(mail, userId);
        labels.map(l => {
            labelsAndMails.deleteLabelFromMail(mail, l, userId);
            const urls = extractUrls(`${mail.subject} ${mail.body}`)
            urls.map(async url => {
                req.body = { url: url };
                await black.createBlacklistEntry(req, res);
            })
        })
    }
    const returnedLabel = labelsAndMails.addLabelToMail(mail, labelToAdd, userId);

    if (returnedLabel == labelToAdd) {
        return res.status(201).end();
    } else {
        return res.status(404).json({ error: 'Label not added' }).end();
    }
}

//get the mailId and the labelId in the params
exports.deleteLabelFromMail = (req, res) => {
    const userId = req.id
    if (!userId) return;
    const labelId = req.params.labelId
    const labelToRemove = getLabelById(labelId, userId);
    if (!labelToRemove) return res.status(404).json({ error: 'Label not found' });

    const mail = Mail.getById(userId, req.params.mailId, labelToRemove);
    if (!mail) return res.status(404).json({ erro: 'Mail not found' });

    if (!labelsAndMails.getLabelFromMailById(mail, labelToRemove, userId)) {
        return res.status(404).json({ error: 'The mail does not belongs to this label' });
    }

    const ret = labelsAndMails.deleteLabelFromMail(mail, labelToRemove, userId);
    if (labelToRemove.name == "spam") {
        const labels = Mail.getLabelsOfMail(mail, userId);
        labels.map(l => {
            const urls = extractUrls(`${mail.subject} ${mail.body}`)
            urls.map(async url => {
                req.body = { url: url };
                await black.deleteBlacklistEntry(req, res);
            })
        })
    }

    if (ret == -1) {
        return res.status(404).json({ error: 'Label not removed' }).end();
    } else {
        return res.status(204).end();
    }
}


exports.getLabelFromMailById = (req, res) => {
    const userId = req.id
    if (!userId) return;
    const labelId = req.params.labelId
    const labelToGet = getLabelById(labelId, userId);
    if (!labelToGet) return res.status(404).json({ error: 'Label not found' });

    const mail = Mail.getById(userId, req.params.mailId, labelToGet);
    if (!mail) return res.status(404).json({ erro: 'Mail not found' });

    const returnedLabel = labelsAndMails.getLabelFromMailById(mail, labelToGet, userId);
    if (returnedLabel == labelToGet) {
        return res.status(200).json(returnedLabel).end();
    } else {
        return res.status(404).json({ error: 'Label not found' }).end();
    }
}
