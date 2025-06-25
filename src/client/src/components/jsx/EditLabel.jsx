import { useRef, useEffect, useState } from "react";
import "../css/EditLabel.css";

function EditLabel({ label, onUpdated, close, active, onActivate }) {
  const [labelName, setLabelName] = useState(label.name);
  const [isEditing, setIsEditing] = useState(false);
  const editRef = useRef(null);

  useEffect(() => {
    if (isEditing && editRef.current) {
      editRef.current.scrollIntoView({ behavior: "smooth", block: "nearest" });
    }
  }, [isEditing]);


  async function editLabelHandler(event) {
    event.preventDefault();

    if (!labelName.trim()) {
      alert("Label name cannot be empty");
      return;
    }

    try {
      const updatedLabel = { name: labelName.trim() };

      const res = await fetch(`/api/labels/${label.id}`, {
        method: "PATCH",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(updatedLabel),
      });

      if (!res.ok) {
        const errorText = await res.text();
        throw new Error(`HTTP ${res.status}: ${errorText}`);
      }

      setIsEditing(false);
      await onUpdated();
      close();
    } catch (error) {
      console.error("Error updating label:", error);
      alert(`Error updating label: ${error.message}`);
    }
  }

  function handleEdit() {
    onActivate();
    setIsEditing(true);
  }

  function handleCancel() {
    setLabelName(label.name);
    setIsEditing(false);
    close();
  }

  if (isEditing) {
    return (
      <div className="editLabelForm" ref={editRef}>
        <form onSubmit={editLabelHandler}>
          <input
            type="text"
            value={labelName}
            onChange={(e) => setLabelName(e.target.value)}
            placeholder="Label Name"
            autoFocus
          />
          <div className="editButtons">
            <button type="submit" className="saveButtonEdit">
              Save
            </button>
            <button
              type="button"
              onClick={handleCancel}
              className="cancelButtonEdit"
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    );
  }
  if (!active) return null;

  return (
    <button className="popupOption editOption" onClick={handleEdit}>
      Edit
    </button>
  );
}

export default EditLabel;
