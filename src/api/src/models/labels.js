const labels = {}
const Mails = require('./mails');
const labelsAndMails = require('./labelsAndMails')
const labelsAndUsers = require('./labelsAndUsers')
const DEFAULT_LABELS = ["starred",  "important", "sent", "drafts", "trash", "all" ];

//function that generates IDs
function IdGenerator() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    const r = Math.random() * 16 | 0;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}

const getLabels = (userId) => {
  if (!labels[userId]) return [];
  const userLabels = []
  const returnLabels = []

  //get the labels of the user
  for (let i = 0; i < labels[userId].length; i++) {
    const label = DEFAULT_LABELS.find(l => l == labels[userId][i].name);
    if (labels[userId][i].userId == userId && !label) {
      userLabels.push(labels[userId][i])
    }
  }

  //prepare the labels to return
  for (let i = 0; i < userLabels.length; i++) {
    const label = {
      id: userLabels[i].id,
      name: userLabels[i].name
    }
    returnLabels.push(label);
  }
  return returnLabels;
}

const getLabelById = (id, userId) => {
  if (!labels[userId]) return null;

  return labels[userId].find(label => label.userId === userId && label.id === id) || null;
};


const createLabel = (name, userId) => {
  if (!labels[userId]) {
    labels[userId] = []
  }
  const label = { id: IdGenerator(), name, userId }
  labelsAndUsers.addLabelToUser(userId, label);
  labels[userId].push(label)
  return label
}

const createFirstLabel = (name, userId) => {
  if (!labels[userId]) {
    labels[userId] = []
  }
  const label = { id: IdGenerator(), name, userId }
  labels[userId].push(label)
  return label
}

const deleteLabelById = (label, userId) => {
  if (label.userId == userId) {
    labelsAndUsers.deleteLabelFromUser(userId, label);
    let mails = Mails.getAllMails(userId);
    mails.map((mail) => {
      const l = labelsAndMails.getLabelFromMailById(mail, label, userId);
      if (l) {
        labelsAndMails.deleteLabelFromMail(mail, l, userId);
      }
    });

    let i = labels[userId].indexOf(label)
    if (i > -1)
      labels[userId].splice(i, 1)
  } else {
    return -1;
  }
}

const patchLabelById = (label, name, userId) => {
  userLabels = getLabels(userId)
  for (let i = 0; i < userLabels.length; i++) {
    if (userLabels[i].name == name) {
      return null;
    }
  }
  label.name = name
  return label;
}

const getLabelByName = (name, userId) => {
  const label = labels[userId].find(l => l.name === name && l.userId === userId);
  if (label.userId == userId) {
    return label;
  } else {
    return null;
  }
}

module.exports = { getLabels, getLabelById, createLabel, deleteLabelById, patchLabelById, getLabelByName, createFirstLabel }