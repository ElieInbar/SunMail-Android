// controllers/mails.js

const Mail = require('../models/mails');
const User = require('../models/users');
const Label = require('../models/labels')
const { validateUrls } = require('../utils/urlUtils');

/**
 * Helper: resolve an email address to a userId.
 * If no matching user is found, returns null.
 */
function resolveToUserId(email) {
  const users = User.getAllUsers();
  const match = users.find(u => u.email === email);
  return match ? match.id : null;
}

/**
 * GET /api/mails/label/:labelName
 * Return the last 50 mails for this user (inbox).
 */
exports.getAllMailsOfLabel = (req, res) => {
  const userId = req.id
  if (!userId) return res.status(404).json({ error: 'User not found' }).end();

  const labelName = req.params.labelName;
  if (!labelName) return res.status(404).json({ error: 'Label name not found' }).end();
  const label = Label.getLabelByName(labelName, userId)
  if (!label) return res.status(404).json({ error: 'Label not found' }).end();

  const mails = Mail.getLast50(userId, label);
  if (!mails) return res.status(404).json({ error: 'Mail not found' }).end();

  return res.status(200).json(mails);
};

/**
 * GET /api/mails
 * Return all the mails that are related to the user except for draft.
 */

exports.getAllMails = (req, res) => {
  const userId = req.id;
  if (!userId) return res.status(404).json({ error: 'User not found' }).end();

  const mails = Mail.getAllMails(userId);
  if (!mails) return res.status(404).json({ error: 'Mails not found' }).end();

  return res.status(200).json(mails).end();
}

/**
 * GET /api/mails/:id
 * Return a single mail by ID for this user’s inbox.
 */
exports.getMailById = (req, res) => {
  const userId = req.id
  if (!userId) return res.status(404).json({ error: 'User not found' }).end();

  const mailId = req.params.id;
  if (!mailId) return res.status(404).json({ error: 'Mail Id not found' });

  const mail = Mail.getMailById(userId, mailId);
  if (!mail) return res.status(404).json({ error: 'Mail not found' });

  return res.status(200).json(mail).end();
};

/**
 * POST /api/mails
 * Create a new mail (inbox + sent) after URL checks.
 */
exports.createMail = async (req, res) => {
  const fromUserId = req.id;
  if (!fromUserId) return;

  const { to, subject, body } = req.body;
  const toUserId = resolveToUserId(to);

  // Create the mail
  const mail = Mail.create(fromUserId, toUserId, subject, body);
  if (!mail) return res.status(400).json({ error: 'Mail not created' })

  return res.status(201).json(mail).end();
};


exports.deleteMail = (req, res) => {
  const userId = req.id;
  if (!userId) return res.status(404).json({ error: 'User not found' }).end();

  const mailId = req.params.id;
  if (!mailId) return res.status(404).json({ error: 'Mail Id not found' });

  const ok = Mail.deleteMail(userId, mailId);
  if (!ok) return res.status(404).json({ error: 'Mail not found' });
  return res.status(204).end();
};

/**
 * GET /api/mails/search/:query
 * Search mails in the user’s inbox.
 */
exports.searchMails = (req, res) => {
  const userId = req.id
  if (!userId) return res.status(404).json({ error: 'User not found' }).end();

  const query = req.params.query;
  if (!query) return res.status(404).json({ error: 'Query not found' });

  const results = Mail.search(userId, query);
  if (!results) return res.status(404).json({ error: "Results of search not found" })

  res.status(200).json(results).end();
};


/**
 * GET /api/mails/:id/read/:labelName
 * Change the read field of the mail in the label
 */
exports.setRead = (req, res) => {
  const userId = req.id;
  if (!userId) return res.status(400).end();

  const receiver = req.body.to
  if (!receiver) return res.status(400).end();

  const mailId = req.params.id;
  if (!mailId) return res.status(404).json({ error: 'Mail Id not found' });

  const labelName = req.params.labelName;
  if (!labelName) return res.status(404).json({ error: 'Label name not found' }).end();
  const label = Label.getLabelByName(labelName, userId);
  if (!label) return res.status(404).json({ error: 'Label not found' }).end();

  const currentMail = Mail.getById(userId, mailId, label);
  if (!currentMail) return res.status(404).json({ error: 'Mail not found' });

  if (currentMail.to != receiver) return res.status(400).end();
  const mail = Mail.setRead(userId, mailId, label)
  if (!mail) {
    return res.status(404).json({ error: 'Error while reading' });
  }
  res.status(204).end();
};

exports.sendMail = (req, res) => {
  const userId = req.id
  if (!userId) return res.status(404).json({ error: 'User not found' }).end();

  const mailId = req.params.id
  if (!mailId) return res.status(404).json({ error: 'Mail not found' }).end();

  const mail = Mail.getMailById(userId, mailId)
  if (!mail.to || !mail.subject || !mail.body) {
    return res.status(400).json({ error: 'Missing fields: to, subject, and body are required' });
  }

  const send = Mail.sendMail(userId, mailId)
  if (!mail) return res.status(400).json({ error: 'Invalid mail' }).end();

  return res.status(201).json({
    id: send.id,
    message: 'Email sent successfully',
    to: send.to,
    subject: send.subject,
    date: send.date,
    labels: send.labels
  });
}

exports.editMail = (req, res) => {
  const userId = req.id
  if (!userId) return res.status(404).json({ error: 'User not found' }).end();

  const { to, subject, body } = req.body
  const mailId = req.params.id
  if (!mailId) return res.status(404).json({ message: 'Mail not found' }).end();

  const mail = Mail.editMail(userId, mailId, to, subject, body);
  if (!mail) return res.status(400).json({ error: "Mail not edited" }).end();

  return res.status(204).json(mail).end();
}