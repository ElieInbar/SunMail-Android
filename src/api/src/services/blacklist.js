// services/blacklist.js

const Blacklist = require('../models/blacklist');

/**
 * Add a new URL to the blacklist
 */
const create = async (url) => {
    try {
        // Check if URL already exists
        const existingUrl = await Blacklist.findOne({ url });
        if (existingUrl) {
            return existingUrl; // Return existing entry instead of error
        }

        const blacklistEntry = new Blacklist({ url });
        await blacklistEntry.save();
        return blacklistEntry;
    } catch (error) {
        console.error('Error creating blacklist entry:', error);
        throw error;
    }
};

/**
 * Remove URL from blacklist
 */
const remove = async (url) => {
    try {
        const result = await Blacklist.deleteOne({ url });
        return result.deletedCount > 0;
    } catch (error) {
        console.error('Error removing blacklist entry:', error);
        throw error;
    }
};

/**
 * Check if URL is blacklisted
 */
const isBlacklisted = async (url) => {
    try {
        const entry = await Blacklist.findOne({ url });
        return !!entry;
    } catch (error) {
        console.error('Error checking blacklist:', error);
        throw error;
    }
};

/**
 * Get all blacklisted URLs
 */
const getAll = async () => {
    try {
        return await Blacklist.find({});
    } catch (error) {
        console.error('Error getting all blacklist entries:', error);
        throw error;
    }
};

module.exports = {
    create,
    remove,
    isBlacklisted,
    getAll
};
