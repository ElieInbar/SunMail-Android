const Mails = require('./mails')
const black = require('./blacklist')

exports.addLabelToMail = (mail, label, userId) => {
    const labels = Mails.getLabelsOfMail(mail, userId);
    Mails.ensureMailbox(labels, userId);
    labels.push(label);
    return labels.at(labels.length - 1)
}

exports.deleteLabelFromMail = (mail, label, userId) => {
    const labels = Mails.getLabelsOfMail(mail, userId);
    Mails.ensureMailbox(labels, userId);
    let i = labels.indexOf(label)
    if (i > -1) {
        labels.splice(i, 1)
    } else {
        return -1
    }
    return 0;
}

exports.getLabelFromMailById = (mail, label, userId) => {
    const labels = Mails.getLabelsOfMail(mail, userId);
    // Mails.ensureMailbox(labels, userId);
    return labels.find(l => l == label);
}

