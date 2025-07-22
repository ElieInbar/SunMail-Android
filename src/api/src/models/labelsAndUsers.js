const User = require('./users')

exports.addLabelToUser = (userId, label) => {
    const user = User.getUserById(userId);
    const labels = User.getLabelsOfUser(user);
    labels.push(label);
    return labels.at(labels.length - 1)
}

exports.getLabelFromUserById = (userId, label) => {
    const user = User.getUserById(userId);
    const labels = User.getLabelsOfUser(user);
    return labels.find(l => l == label);
}

exports.deleteLabelFromUser = (userId, label) => {
    const user = User.getUserById(userId);
    const labels = User.getLabelsOfUser(user);
    let i = labels.indexOf(label)
    if (i > -1) {
        labels.splice(i, 1)
    } else {
        return -1
    }
    return 0;
}