// controllers/mails.js

const mailService = require('../services/mails');
const userService = require('../services/users');
const labelService = require('../services/labels');

/**
 * Helper: resolve an email address to a userId.
 * If no matching user is found, returns null.
 */
async function resolveToUserId(email) {
  const users = await userService.getAllUsers();
  const match = users.find(u => u.email === email);
  return match ? match.id : null;
}

/**
 * GET /api/mails/label/:labelName
 * Return the last 50 mails for this user with specific label.
 */
exports.getAllMailsOfLabel = async (req, res) => {
  try {
    const userId = req.id;
    if (!userId) return res.status(404).json({ error: 'User not found' });

    const labelName = req.params.labelName;
    if (!labelName) return res.status(404).json({ error: 'Label name not found' });

    const labelRet = await labelService.getLabelByName(labelName, userId);
    if (!labelRet) return res.status(404).json({ error: 'Label not found' });

    const mails = await mailService.getLast50(userId, labelName);
    return res.status(200).json(mails);
  } catch (error) {
    console.error('Error getting mails of label:', error);
    return res.status(500).json({ error: 'Internal server error' });
  }
};

/**
 * GET /api/mails
 * Return all the mails that are related to the user except for draft.
 */
exports.getAllMails = async (req, res) => {
  try {
    const userId = req.id;
    if (!userId) return res.status(404).json({ error: 'User not found' });

    const mails = await mailService.getAllMails(userId);
    return res.status(200).json(mails);
  } catch (error) {
    console.error('Error getting all mails:', error);
    return res.status(500).json({ error: 'Internal server error' });
  }
};

/**
 * GET /api/mails/:id
 * Return a single mail by ID for this user’s inbox.
 */
exports.getMailById = async (req, res) => {
  try {
    const userId = req.id;
    if (!userId) return res.status(404).json({ error: 'User not found' });

    const mailId = req.params.id;
    if (!mailId) return res.status(404).json({ error: 'Mail Id not found' });

    const mail = await mailService.getMailById(userId, mailId);
    if (!mail) return res.status(404).json({ error: 'Mail not found' });

    return res.status(200).json(mail);
  } catch (error) {
    console.error('Error getting mail by ID:', error);
    return res.status(500).json({ error: 'Internal server error' });
  }
};

/**
 * POST /api/mails
 * Create a new mail (draft) - can be empty.
 */
exports.createMail = async (req, res) => {
  try {
    const fromUserId = req.id;
    if (!fromUserId) return res.status(401).json({ error: 'User not authenticated' });

    const { to = '', subject = '', body = '' } = req.body || {};

    // For drafts, we don't need to validate the recipient
    let toUserId = '';
    if (to && to.trim()) {
      toUserId = await resolveToUserId(to);
      // If recipient is provided but not found, still create draft with email
      if (!toUserId) {
        toUserId = to; // Store the email even if user doesn't exist yet
      }
    }

    // Create the mail (draft)
    const mail = await mailService.create(fromUserId, toUserId, subject, body);
    if (!mail) return res.status(400).json({ error: 'Mail not created' });

    return res.status(201).json(mail);
  } catch (error) {
    console.error('Error creating mail:', error);
    return res.status(500).json({ error: 'Internal server error' });
  }
};


exports.deleteMail = async (req, res) => {
  try {
    const userId = req.id;
    if (!userId) return res.status(404).json({ error: 'User not found' });

    const mailId = req.params.id;
    if (!mailId) return res.status(404).json({ error: 'Mail Id not found' });

    const ok = await mailService.deleteMail(userId, mailId);
    if (!ok) return res.status(404).json({ error: 'Mail not found' });
    return res.status(204).end();
  } catch (error) {
    console.error('Error deleting mail:', error);
    return res.status(500).json({ error: 'Internal server error' });
  }
};

/**
 * GET /api/mails/search/:query
 * Search mails in the user’s inbox.
 */
exports.searchMails = async (req, res) => {
  try {
    const userId = req.id;
    if (!userId) return res.status(404).json({ error: 'User not found' });

    const query = req.params.query;
    if (!query) return res.status(404).json({ error: 'Query not found' });

    const results = await mailService.search(userId, query);
    return res.status(200).json(results);
  } catch (error) {
    console.error('Error searching mails:', error);
    return res.status(500).json({ error: 'Internal server error' });
  }
};


/**
 * GET /api/mails/:id/read/:labelName
 * Change the read field of the mail in the label
 */
exports.setRead = async (req, res) => {
  try {
    const userId = req.id;
    if (!userId) return res.status(400).json({ error: 'User not authenticated' });

    const receiver = req.body.to;
    if (!receiver) return res.status(400).json({ error: 'Receiver not provided' });

    const mailId = req.params.id;
    if (!mailId) return res.status(404).json({ error: 'Mail Id not found' });

    const labelName = req.params.labelName;
    if (!labelName) return res.status(404).json({ error: 'Label name not found' });

    const mail = await mailService.setRead(userId, mailId, labelName);
    if (!mail) {
      return res.status(404).json({ error: 'Error while reading' });
    }
    return res.status(204).end();
  } catch (error) {
    console.error('Error setting mail as read:', error);
    return res.status(500).json({ error: 'Internal server error' });
  }
};

exports.sendMail = async (req, res) => {
  try {
    const userId = req.id;
    if (!userId) return res.status(404).json({ error: 'User not found' });

    const mailId = req.params.id;
    if (!mailId) return res.status(404).json({ error: 'Mail not found' });

    const mail = await mailService.getMailById(userId, mailId);
    if (!mail.to || !mail.subject || !mail.body) {
      return res.status(400).json({ error: 'Missing fields: to, subject, and body are required' });
    }

    const send = await mailService.sendMail(userId, mailId);
    if (!send) return res.status(400).json({ error: 'Invalid mail' });

    return res.status(201).json({
      id: send.id,
      message: 'Email sent successfully',
      to: send.to,
      subject: send.subject,
      date: send.date,
      labels: send.labels
    });
  } catch (error) {
    console.error('Error sending mail:', error);
    return res.status(500).json({ error: 'Internal server error' });
  }
};

exports.editMail = async (req, res) => {
  try {
    const userId = req.id;
    if (!userId) return res.status(404).json({ error: 'User not found' });

    const { to, subject, body } = req.body;
    const mailId = req.params.id;
    if (!mailId) return res.status(404).json({ message: 'Mail not found' });

    const mail = await mailService.editMail(userId, mailId, to, subject, body);
    if (!mail) return res.status(400).json({ error: "Mail not edited" });

    return res.status(200).json(mail);
  } catch (error) {
    console.error('Error editing mail:', error);
    return res.status(500).json({ error: 'Internal server error' });
  }
};
