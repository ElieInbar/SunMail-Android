const labels = require('../models/labels')

exports.getLabels = (req, res) => {
    const user = req.id
    if (!user) return res.status(404).json({ error: 'User not found' }).end();

    const Labels = labels.getLabels(user)
    if (!Labels) return res.status(404).json({ error: 'Labels not found' }).end();
    return res.status(200).json(Labels).end();
}

exports.createLabel = (req, res) => {
    const user = req.id
    if (!user) return res.status(404).json({ error: 'User not found' }).end();

    const { name } = req.body
    if (!name)
        return res.status(400).json({ error: 'Name is required' })

    const Labels = labels.getLabels(user)
    if (!Labels) return res.status(404).json({ error: 'Labels not found' }).end();

    if (Labels.find(l => l.name == name)) {
        return res.status(400).json({ error: 'Label already exists' });
    }
    const label = labels.createLabel(name, user)
    if (!label) return res.status(400).json({ error: 'Label not created' }).end();

    return res.status(201).location(`/api/labels/${label.id}`).json(label).end()
}

exports.getLabelById = (req, res) => {
    const user = req.id
    if (!user) return res.status(404).json({ error: 'User not found' }).end();

    const label = labels.getLabelById(req.params.id, user)
    if (!label)
        return res.status(404).json({ error: 'Label not found' })
    res.status(200).json(label).end();
}

exports.patchLabelById = (req, res) => {
    const user = req.id
    if (!user) return res.status(404).json({ error: 'User not found' }).end();

    const label = labels.getLabelById(req.params.id, user)
    if (!label)
        return res.status(404).json({ error: 'Label not found' })
    const { name } = req.body
    if (!name)
        return res.status(400).json({ error: 'Name is required' })
    const check = labels.patchLabelById(label, name, user)
    if (!check) {
        return res.status(400).json({ error: 'Label already exists' })
    }
    res.status(204).json(check).end();
}

exports.deleteLabelById = (req, res) => {
    const user = req.id
    if (!user) return res.status(404).json({ error: 'User not found' }).end();

    const label = labels.getLabelById(req.params.id, user)
    if (!label)
        return res.status(404).json({ error: 'Label not found' })
    if (labels.deleteLabelById(label, user) == -1) {
        return res.status(404).json({ error: 'Label not found' })
    }
    return res.status(204).end()
}

exports.getLabelByName = (req, res) => {
    const user = req.id;
    if (!user) return res.status(404).json({ error: 'User not found' }).end();

    const labelName = req.params.name;
    if (!labelName) return res.status(404).json({ error: 'Label name not found' }).end();

    const label = labels.getLabelByName(labelName, user);
    if (!label) {
        return res.status(404).json({ error: 'Label not found' });
    }
    return res.status(200).json(label).end()
}
