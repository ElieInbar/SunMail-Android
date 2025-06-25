// src/api/src/models/blacklist.js

const store = []; //url

/**
 * Add a new URL to the blacklist.
 * And return the entry { url }.
 */
function create(url) {
  store.push(url);
  return { url };
}

/**
 * Remove the url from the list.
 * Return true if the entry was found and removed, false otherwise.
 */
function remove(url) {
  const index = store.indexOf(url);
  if (index !== -1) {
    store.splice(index, 1); 
    return true;
  }
  return false;
}

module.exports = { create, remove };
