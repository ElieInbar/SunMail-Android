import React, { useContext, useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { MailContext } from '../../contexts/MailContext';
import Mail from '../../components/jsx/Mail';
import '../css/SearchResultsPage.css'; // Assuming you'll create this CSS file

function SearchResultsPage() {
  const { allMails, fetchMails } = useContext(MailContext);
  const [filteredMails, setFilteredMails] = useState([]);
  const location = useLocation();
  const searchQuery = new URLSearchParams(location.search).get('q');

  useEffect(() => {
    async function searchMails() {
      if (searchQuery) {
        const lowerCaseQuery = searchQuery.toLowerCase();
        let results;
        const res = await fetch(`/api/mails/search/${lowerCaseQuery}`, {
          credentials: "include"
        });

        if (res.status === 404) {
          results = allMails.filter(mail =>
            (mail.fromUser && mail.fromUser.name.toLowerCase().includes(lowerCaseQuery)) ||
            (mail.toUser && mail.toUser.name.toLowerCase().includes(lowerCaseQuery))
          );
        } else {
          const resultsFromApi = await res.json();
          const extra = allMails.filter(mail =>
            (mail.fromUser && mail.fromUser.name.toLowerCase().includes(lowerCaseQuery)) ||
            (mail.toUser && mail.toUser.name.toLowerCase().includes(lowerCaseQuery))
          );

          const merged = [...resultsFromApi, ...extra].filter(
            (mail, idx, arr) => arr.findIndex(m => m.id === mail.id) === idx
          );
          setFilteredMails(merged);
        }
      } else {
        setFilteredMails([]); // Clear results if no query
      }
    }
    searchMails()
  }, [searchQuery, allMails]);

  return (
    <div className="search-results-container">
      <h2>Search Results for "{searchQuery}"</h2>
      {filteredMails.length > 0 ? (
        <div className="allMails-list">
          {filteredMails.map(mail => (
            <Mail key={mail.id} mail={mail} fetchMails={fetchMails} />
          ))}
        </div>
      ) : (
        <p>No results found.</p>
      )}
    </div>
  );
}

export default SearchResultsPage;
