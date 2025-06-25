import { useState, useContext, useEffect } from "react";
import "../css/ComposeWindow.css";
import { MailContext } from "../../contexts/MailContext";
import { useNavigate } from "react-router-dom";


export default function ComposeWindow() {

  const [isMaximized, setIsMaximized] = useState(true);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");
  const navigate = useNavigate();
  const { formData, setFormData, fetchAllMails, setTypeOfDraft, draftId, setDraftId, typeOfDraft, isComposeOpen, isMinimized, currentFolder, fetchMails, setIsComposeOpen, setIsMinimized } = useContext(MailContext);

  useEffect(() => {
    async function createDraft() {
      const res = await fetch(`/api/mails`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify(formData)
      });
      if (res.status !== 201) {
        throw new Error(`Error: Response or forward not created`);
      }
      const draft = await res.json()
      setDraftId(draft.id)
    }

    if (!isComposeOpen) return;
    if (typeOfDraft === "new") {
      setFormData({ to: "", subject: "", body: "" });
    } else if (typeOfDraft === "reply" || typeOfDraft === "forward") {
      createDraft();

    } else {
      getDraft(draftId);
    }
  }, [isComposeOpen, typeOfDraft]);

  const onClose = () => {
    setIsComposeOpen(false);
    setIsMinimized(false);
    setTypeOfDraft(null);
  };

  const onMinimize = () => {
    setIsMinimized(!isMinimized);
  };

  async function getDraft(draftId) {
    const res = await fetch(`/api/mails/${draftId}`, {
      credentials: "include"
    });
    if (res.status !== 200) {
      const errorText = await res.text();
      throw new Error(`Error: ${res.status}: ${errorText}`);
    }
    const draft = await res.json();

    var user = null;
    if (draft.to) {
      const resp = await fetch(`/api/users/${draft.to}`, {
        credentials: "include",
      });
      if (resp.status !== 200) {
        alert("Error");
        return;
      }
      user = await resp.json();
    }
    setFormData({
      to: user ? user.email : "",
      subject: draft.subject,
      body: draft.body,
    });
  }

  const handleInputChange = async (e) => {
    const { name, value } = e.target;
    const newForm = { ...formData, [name]: value };
    setFormData(newForm);
    if (error) setError("");
    try {
      const response = await fetch(`/api/mails/${draftId}`, {
        method: "PATCH",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include", // Important for cookies/auth
        body: JSON.stringify(newForm),
      })
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Error: ${response.status}: ${errorText}`);
      }
      setError("");
    } catch (error) {
      setError("Error: Invalid email.");
    }
  };

  const resetAndClose = () => {
    setIsMaximized(true);
    setError("");
    onClose();
    if (currentFolder === "drafts") {
      navigate('..');
    }
  };

  const handleSend = async () => {
    // Basic validation
    if (!formData.to.trim()) {
      setError("Please enter a recipient");
      return;
    }
    if (!formData.subject.trim()) {
      setError("Please enter a subject");
      return;
    }
    if (!formData.body.trim()) {
      setError("Please enter a message");
      return;
    }

    setIsLoading(true);
    setError("");
    try {
      const response = await fetch(`/api/mails/${draftId}/send`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include", // Important for cookies/auth
      });

      if (!response.ok) {
        throw new Error("Error while sending");
      }

      // Success!
      await fetchMails(currentFolder);
      fetchAllMails()
      resetAndClose(); // Close and reset
      setFormData({ to: "", subject: "", body: "" });

    } catch (err) {
      console.error("Error sending email:", err);
      setError(err.message || "Error while sending the message");
    } finally {
      setIsLoading(false);
    }
  };

  const handleMaximize = () => {
    if (isMinimized) {
      onMinimize();
      setIsMaximized(true);
    } else {
      setIsMaximized(!isMaximized);
    }
  };

  const handleMinimizeClick = () => {
    if (isMinimized) {
      onMinimize();
      setIsMaximized(false);
    } else {
      if (isMaximized) setIsMaximized(false);
      onMinimize();
    }
  };

  if (!isComposeOpen) return null;

  return (
    <div
      className={`compose-window ${isMinimized ? "minimized" : ""} ${isMaximized && !isMinimized ? "maximized" : ""
        }`}
    >
      <div className="compose-header">
        <span className="compose-title">New message</span>
        <div className="compose-controls">
          <button
            onClick={handleMinimizeClick}
            className="control-btn"
            title="Minimize"
          >
            <i className="bi bi-dash-lg"></i>
          </button>
          <button
            onClick={handleMaximize}
            className="control-btn"
            title={isMaximized ? "Normal size" : "Maximize"}
          >
            {isMaximized ? <i className="bi bi-arrows-angle-contract"></i> : <i className="bi bi-arrows-angle-expand"></i>}
          </button>
          <button onClick={resetAndClose} className="control-btn" title="Close">
            <i className="bi bi-x"></i>
          </button>
        </div>
      </div>

      {!isMinimized && (
        <div className="compose-content">

          <div className="compose-fields">
            {["to", "subject"].map((field) => (
              <div key={field} className="field-row">
                <input
                  type={field === "to" ? "email" : "text"}
                  name={field}
                  placeholder={field === "to" ? "To" : "Subject"}
                  value={formData[field] ?? ""}
                  onChange={handleInputChange}
                  className="compose-input"
                  disabled={isLoading}
                />

              </div>
            ))}
            {error && <div className="error-message">{error}</div>}
          </div>

          <div className="compose-body">
            <textarea
              name="body"
              value={formData.body}
              onChange={handleInputChange}
              className="compose-textarea"
              placeholder="Write your message..."
              disabled={isLoading}
            />
          </div>

          <div className="compose-footer">
            <button
              onClick={handleSend}
              className="send-btn"
              disabled={isLoading}
            >
              {isLoading ? "Sending..." : "Send"}
            </button>

          </div>
        </div>
      )}
    </div>
  );
}


