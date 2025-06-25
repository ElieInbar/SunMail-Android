// utils/urlUtils.js

const { sendRawCommand } = require('./clientUtils.js');

/**
 * Extract all unique URLs from a given text.
 * Recognizes links starting with "http://", "https://", or "www.".
 */
function extractUrls(text) {
const urlRegex = /\b((https?:\/\/)?(www\.)?[\w-]{1,63}\.[a-z]{2,}(\/\S*)?)\b(?!@)(?<![\w.-]+@[\w.-]+\.[a-z]{2,})/gi;
  const matches = [];
  let match;

  while ((match = urlRegex.exec(text)) !== null) {
    let url = match[0].replace(/[.,;:!?]+$/, "");
    matches.push(url);
  }

  // Return only unique URLs
  return Array.from(new Set(matches));
}

/**
 * Validate all extracted URLs from subject + body are not blacklisted.
 * Throws:
 *   - { status: 400, message: 'Blacklisted URL', url } if any URL starts with a "200" response,
 *   - { status: 500, message: 'URL verification service unavailable' } if the check fails at all.
 */
async function validateUrls(subject, body) {
  const urls = extractUrls(`${subject} ${body}`);
  try {
    for (const url of urls) {
      const response = await sendRawCommand(`GET ${url}`);
      // If `response.startsWith('200')`, we consider it blacklisted
      if (response.endsWith("true")) {
        return 1;
      }
    }
  } catch (err) {
    const e = new Error('URL verification service unavailable');
    e.status = 500;
    throw e;
  }
  return 0;
}

module.exports = {
  extractUrls,
  validateUrls,
};
