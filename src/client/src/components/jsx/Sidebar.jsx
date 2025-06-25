import { useState, useEffect, useContext } from "react";
import { useNavigate } from "react-router-dom";

import { MailContext } from "../../contexts/MailContext";
import "../css/Sidebar.css";
import AddLabel from "./AddLabel";
import LabelMenu from "./LabelMenu";
import ComposeWindow from "./ComposeWindow";

// List of primary folders shown by default in the sidebar
const primaryFolders = [
  {
    key: "inbox",
    label: "Inbox",
    icon: <i className="bi bi-inbox"></i>,
    countKey: "inbox",
  },
  { key: "starred", label: "Starred", icon: <i className="bi bi-star"></i> },
  // {
  //   key: 
  //   label: "Snoozed",
  //   icon: <AiOutlineClockCircle size={20} />,
  // },
  { key: "important", label: "Important", icon: <i className="bi bi-bookmark"></i> },
  { key: "sent", label: "Sent", icon: <i className="bi bi-send"></i> },
  {
    key: "drafts",
    label: "Drafts",
    icon: <i className="bi bi-file-earmark"></i>,
    countKey: "drafts",
  },
];

// List of secondary folders shown when "More" is expanded
const secondaryFolders = [
  // { key: "chats", label: "All chats", icon: <BsChatLeftText size={20} /> },
  // { key: "scheduled", label: "Scheduled", icon: <MdScheduleSend size={20} /> },
  { key: "all", label: "All mails", icon: <i className="bi bi-envelope"></i> },
  { key: "spam", label: "Spam", icon: <i className="bi bi-exclamation-octagon"></i> },
  { key: "trash", label: "Trash", icon: <i className="bi bi-trash"></i> },
  // {
  //   key: "manage",
  //   label: "Manage labels",
  //   icon: <MdOutlineSettings size={20} />,
  // },
  // { key: 'create', label: 'Create label', icon: <AiOutlinePlus size={20} /> },
];



// Component to render a list of folders (either primary or secondary)
function FolderList({ folders, currentFolder, onSelectFolder, counts }) {
  return (
    <ul className="sidebar-folders">
      {folders.map(({ key, label, icon, countKey }) => (
        <li
          key={key}
          className={`sidebar-folder-item${currentFolder === key ? " active" : ""
            }`}
          onClick={() => onSelectFolder(key)}
        >
          {/* Folder icon */}
          <span className="folder-icon">{icon}</span>
          {/* Folder label */}
          <span className="folder-label">{label}</span>
          {/* Show count if available and greater than 0 */}
          {countKey && counts?.[countKey] > 0 && (
            <span className="folder-count">{counts[countKey]}</span>
          )}
        </li>
      ))}
    </ul>
  );
}

// Main Sidebar component
export default function Sidebar({
  isOpen,
  counts,
}) {
  const { setTypeOfDraft, setDraftId, currentFolder, setCurrentFolder, setIsComposeOpen, setIsMinimized } = useContext(MailContext);

  const [labels, setLabels] = useState([]);

  const navigate = useNavigate();


  function handleMenuClicked(name) {
    setCurrentFolder(name)
    navigate(`../${name}`)
  }

  async function fetchLabels() {
    const res = await fetch("/api/labels", {
      credentials: "include",
    });
    const json = await res.json();
    setLabels(json);
  }

  useEffect(() => {
    fetchLabels();
  }, []);

  // State to control whether secondary folders are shown
  const [showMore, setShowMore] = useState(false);
  const [createLabelClicked, setCreateLabelClicked] = useState(false);

  async function createDraft() {
    const res = await fetch("/api/mails", {
      method: "POST",
      credentials: "include",
      headers: { "Content-Type": "application/json" },
      // body: JSON.stringify({ to: ' ', subject: ' ', body: ' ' })
    })
    const draft = await res.json();
    setDraftId(draft.id);
  }

  const handleOpenCompose = () => {
    createDraft();
    setIsComposeOpen(true);
    setIsMinimized(false);
    setTypeOfDraft("new");
  };

  return (
    <>
      <nav className={`sidebar surface${isOpen ? "" : " collapsed"}`}>
        {/* Compose new message button */}
        <button
          className="sidebar-compose-btn btn-float"
          title="New message"
          onClick={handleOpenCompose}
        >
          <span className="folder-icon">
            <i className="bi bi-pencil-square"></i>
          </span>
          <span className="compose-label">New message</span>
        </button>

        {/* Render primary folders */}
        <FolderList
          folders={primaryFolders}
          currentFolder={currentFolder}
          onSelectFolder={handleMenuClicked}
          counts={counts}
        />

        {/* Toggle to show/hide secondary folders */}
        <div
          className="sidebar-folder-item sidebar-more-toggle"
          onClick={() => setShowMore((v) => !v)}
        >
          <span className="folder-icon">
            {showMore ? (
              <i className="bi bi-chevron-down"></i>
            ) : (
              <i className="bi bi-plus"></i>
            )}
          </span>
          <span className="folder-label">{showMore ? "Less" : "More"}</span>
        </div>

        {/* Render secondary folders if showMore is true */}
        {showMore && (
          <FolderList
            folders={secondaryFolders}
            currentFolder={currentFolder}
            onSelectFolder={handleMenuClicked}
            counts={counts}
          />
        )}

        {showMore && (
          <>
            <button
              className="sidebar-create-label"
              onClick={() => setCreateLabelClicked((v) => !v)}
            >
              <span className="folder-icon">
                <i className="bi bi-plus"></i>
              </span>
              Create label
            </button>
            {createLabelClicked && <AddLabel fetchLabels={fetchLabels} />}
            <LabelMenu labels={labels} fetchLabels={fetchLabels} />
          </>
        )}
      </nav>
      <ComposeWindow />
    </>
  );
}
