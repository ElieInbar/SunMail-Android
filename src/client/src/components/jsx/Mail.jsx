import { useState, useEffect, useContext, useCallback } from "react";
import "../css/Mail.css";
import { AuthContext } from '../../contexts/AuthContext';
import ComposeWindow from "./ComposeWindow";
import { MailContext } from "../../contexts/MailContext";
import { useLocation } from 'react-router-dom';
import { useNavigate } from "react-router-dom";

function Mail({ mail }) {
  const [isStarred, setIsStarred] = useState(false);
  const [isImportant, setisImportant] = useState(false);
  const [isDraft, setIsDraft] = useState(null);
  const [user, setUser] = useState(null);
  const [showCompose, setShowCompose] = useState(false);
  const { username } = useContext(AuthContext);
  const { currentFolder, setCurrentFolder, fetchMails, fetchAllMails } = useContext(MailContext)
  const location = useLocation();
  const navigate = useNavigate();

  async function checkIfDraft() {
    const response = await fetch(`/api/users/by-username/${username}`);
    if (!response.ok) throw new Error('User not found');

    const currUser = await response.json();
    if (mail.labels?.some(label => label.name === "drafts" && label.userId === currUser.id)) {
      setIsDraft(true);
    } else {
      setIsDraft(false)
    }
  }

  async function checkStarred() {
    const response = await fetch(`/api/users/by-username/${username}`);
    if (!response.ok) throw new Error('User not found');

    const currUser = await response.json();
    if (mail.labels?.some(label => label.name === "starred" && label.userId === currUser.id)) {
      setIsStarred(true);
    } else {
      setIsStarred(false)
    }
  }

  async function checkImportant() {
    const response = await fetch(`/api/users/by-username/${username}`);
    if (!response.ok) throw new Error('User not found');

    const currUser = await response.json();
    if (mail.labels?.some(label => label.name === "important" && label.userId === currUser.id)) {
      setisImportant(true);
    } else {
      setisImportant(false)
    }
  }


  const handleStarClicked = useCallback(async () => {
    if (isDraft) return;
    setIsStarred(!isStarred);

    const res1 = await fetch(`/api/labels/name/starred`, {
      credentials: "include",
    })
    if (res1.status !== 200) {
      throw new Error('Label not found')
    }
    const label = await res1.json();
    if (!isStarred) {
      const res2 = await fetch(`/api/labelsAndMails/${mail.id}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify({ labelId: label.id })
      })

      if (res2.status !== 201) {
        throw new Error('Error while adding to star')
      }
      fetchMails(currentFolder)
      fetchAllMails()
    } else {
      const res2 = await fetch(`/api/labelsAndMails/${mail.id}/${label.id}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify({ labelId: label.id })
      })

      if (res2.status !== 204) {
        throw new Error('Error while removing from star')
      }
      fetchMails(currentFolder)
      fetchAllMails()
    }
  }, [isDraft, isStarred, currentFolder]);

  const handleImportantClicked = useCallback(async () => {
    if (isDraft) return;

    setisImportant(!isImportant);

    const res1 = await fetch(`/api/labels/name/important`, {
      credentials: "include",
    })
    if (res1.status !== 200) {
      throw new Error('Label not found')
    }
    const label = await res1.json();
    if (!isImportant) {
      const res2 = await fetch(`/api/labelsAndMails/${mail.id}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify({ labelId: label.id })
      })

      if (res2.status !== 201) {
        throw new Error('Error while adding to important')
      }
      fetchMails(currentFolder)
      fetchAllMails()
    } else {
      const res2 = await fetch(`/api/labelsAndMails/${mail.id}/${label.id}`, {
        method: "DELETE",
        headers: {
          "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify({ labelId: label.id })
      })

      if (res2.status !== 204) {
        throw new Error('Error while removing from important')
      }
      fetchMails(currentFolder)
      fetchAllMails()
    }
  }, [isDraft, isImportant, currentFolder]);


  async function handleClicked(e) {
    const response = await fetch(`/api/users/by-username/${username}`);
    if (!response.ok) throw new Error('User not found');

    const currUser = await response.json();

    if (location.pathname.startsWith("/search")) {
      const label = mail.labels?.find(label => label.userId === currUser.id);
      setCurrentFolder(label.name);
      navigate(`/${currentFolder}/${mail.id}`)
    }

    if (mail.to === currUser.id && currentFolder !== "drafts") {
      const res = await fetch(`/api/mails/${mail.id}/read/${currentFolder}`, {
        method: "PATCH",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ to: mail.to })
      });
      if (res.status !== 204) {
        return null;
      }
      if (e.target.closest(".selectIcon, .starIcon, .importantIcon, .deleteIcon")) return;
    }
    fetchMails(currentFolder);
    fetchAllMails()
  }

  useEffect(() => {
    async function fetchUser() {
      const res = await fetch(`/api/users/${mail.from}`, {
        credentials: "include",
      });
      if (res.status !== 200) {
        alert("Error");
        setUser(null);
        return;
      }
      const json = await res.json();
      setUser(json);
    }
    fetchMails(currentFolder)
    fetchAllMails()
    fetchUser();
  }, [mail.from]);



  useEffect(() => {
    checkIfDraft();
    checkStarred();
    checkImportant();
  }, [currentFolder]);

  const handleClick = (e) => {
    if (currentFolder === "drafts") {
      setShowCompose(true);
    } else {
      handleClicked(e);
    }
  };

  const handleDelete = async (e) => {
    e.stopPropagation();
    await fetch(`/api/mails/${mail.id}`, {
      method: "DELETE",
      credentials: "include",
    });
    fetchMails(currentFolder);
    fetchAllMails()
  };


  return (
    <>
      <div
        className={`mailRow ${mail.read ? "read" : "unread"}`}
        onClick={handleClick}
        tabIndex={0}
        role="button"
      >
        <button
          className="starIcon"
          onClick={(e) => {
            e.stopPropagation();
            handleStarClicked();
          }}
        >
          {isStarred ? (
            <span id="starOn">
              <i className="bi bi-star-fill"></i>
            </span>
          ) : (
            <i className="bi bi-star"></i>
          )}
        </button>
        <button
          className="importantIcon"
          onClick={(e) => {
            e.stopPropagation();
            handleImportantClicked();
          }}
        >
          {isImportant ? (
            <span id="importantOn">
              <i className="bi bi-bookmark-fill"></i>
            </span>
          ) : (
            <i className="bi bi-bookmark"></i>
          )}
        </button>
        <button className="deleteIcon" onClick={handleDelete}>
          <i className="bi bi-trash"></i>
        </button>
        <span className="mailSender">{user ? user.name : "Loading..."}</span>
        <div className="mailContent">
          <span className="mailSubject">{mail.subject}</span>
          <span className="mailBody"> - {mail.body}</span>
        </div>
      </div>
      {showCompose && (
        <ComposeWindow />
      )}
    </>
  );
}

export default Mail;
