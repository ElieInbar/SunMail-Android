const labelService = require('../services/labels')

exports.getLabels = async (req, res) => {
    const userId = req.id
    if (!userId) return res.status(404).json({ error: 'User not found' }).end();

    return res.status(200).json(await labelService.getLabels(userId)).end();
}

exports.createLabel = async (req, res) => {
    const userId = req.id
    if (!userId) return res.status(404).json({ error: 'User not found' }).end();

    const { name } = req.body
    if (!name) return res.status(400).json({ error: 'Name is required' })

    return res.status(201).json(await labelService.createLabel(name, userId)).end()
}

exports.getLabelById = async (req, res) => {
    const userId = req.id
    if (!userId) return res.status(404).json({ error: 'User not found' }).end();

    const id = req.params.id;
    const label = await labelService.getLabelById(id, userId);
    if (!label) return res.status(404).json({ error: 'Label not found' }).end();

    res.status(200).json(label).end();
}

exports.patchLabelById = async (req, res) => {
    const userId = req.id
    if (!userId) return res.status(404).json({ error: 'User not found' }).end();

    const id = req.params.id;
    const { name } = req.body
    if (!name)
        return res.status(400).json({ error: 'Name is required' })

    const check = await labelService.patchLabelById(id, name, userId);
    if (!check) {
        return res.status(400).json({ error: 'Label not found' })
    }
    res.status(204).json(check).end();
}

exports.deleteLabelById = async (req, res) => {
    const userId = req.id
    if (!userId) return res.status(404).json({ error: 'User not found' }).end();

    const id = req.params.id;
    const label = await labelService.deleteLabelById(id, userId);

    if (!label) {
        return res.status(404).json({ error: 'Label not found' })
    }
    return res.status(204).end()
}

exports.getLabelByName = async (req, res) => {
    try {
        const userId = req.id;
        if (!userId) return res.status(401).json({ error: 'User not authenticated' });

        const labelName = req.params.name;
        if (!labelName) return res.status(400).json({ error: 'Label name is required' });

        const label = await labelService.getLabelByName(labelName, userId);
        if (!label) {
            return res.status(404).json({ error: 'Label not found' });
        }
        return res.status(200).json(label);
    } catch (error) {
        console.error('Error getting label by name:', error);
        return res.status(500).json({ error: 'Internal server error' });
    }
};
