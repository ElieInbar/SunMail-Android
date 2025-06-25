// src/api/src/controllers/blacklist.js
const { create, remove } = require('../models/blacklist');
const { sendRawCommand } = require('../utils/clientUtils');


/**
 * POST /api/blacklist
 * Body JSON: { "url": "http://example.com" }
 * - Send "Post <url>" to the C++ server.
 * - If C++ returns 201 Created or 200 Ok, add the URL locally.
 * - Otherwise return 400 Bad Request.
 */
async function createBlacklistEntry(req, res) {
  const userId = req.id
  if (!userId) return;
  const { url } = req.body;
  if (!url) {
    return res.status(400).json({ error: 'URL is required' });
  }

  try {
    // 1) send "POST <url>" to the C++ server
    const response = await sendRawCommand(`POST ${url}`);
    // 2) check if it's a success (201 or 200 at the start)
    if (/^201\b/.test(response)) {
      // Local addition
      create(url);
      // return the 201 status if created
      return res.status(201).end();
    } else {
      // Otherwise, return 400 Bad Request
      return res.status(400).json({ error: 'Failed to add URL to blacklist', detail: response });
    }
  } catch (err) {
    console.error('Error in createBlacklistEntry:', err);
    return res.status(500).json({ error: 'Blacklist service unavailable' });
  }
}

/**
 * DELETE /api/blacklist/:id
 * - Search the URL to delete by its ID in the local model.
 * - If not found, return 404.
 * - Otherwise, send "DELETE <url>" to the C++ filter.
 *   * If C++ returns 200 Ok or 204 No Content, we delete it locally.
 *   * Otherwise 400 Bad Request.
 */
async function deleteBlacklistEntry(req, res) {
  const userId = req.id
  if (!userId) return;

  // Get the URL by ID
  const { url } = req.body;
  if (!url) {
    return res.status(404).json({ error: 'Blacklist entry not found' });
  }

  try {
    // Send "DELETE <url>" to the C++ server
    const response = await sendRawCommand(`DELETE ${url}`);
    // The filter returns “204 No Content”
    if (/^204\b/.test(response)) {
      remove(url);
      return res.status(204).end();
    } 
    // else {
    //   return res.status(400).json({ error: 'Failed to remove URL from blacklist', detail: response });
    // }
  } catch (err) {
    console.error('Error in deleteBlacklistEntry:', err);
    return res.status(500).json({ error: 'Blacklist service unavailable' });
  }
}

module.exports = { createBlacklistEntry, deleteBlacklistEntry };
