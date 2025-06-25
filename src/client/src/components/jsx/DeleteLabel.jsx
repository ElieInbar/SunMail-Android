import { useRef, useEffect, useState } from "react";
import "../css/DeleteLabel.css";

function DeleteLabel({ labelId, onDeleted, close, active, onActivate }) {
  const [isConfirming, setIsConfirming] = useState(false);
  const confirmRef = useRef(null);

  useEffect(() => {
    if (isConfirming && confirmRef.current) {
      confirmRef.current.scrollIntoView({ behavior: "smooth", block: "nearest" });
    }
  }, [isConfirming]);

  async function deleteLabelHandler() {
    const res = await fetch(`/api/labels/${labelId}`, {
      method: "DELETE",
      credentials: "include",
    });

    if (res.status !== 204) {
      alert("Error deleting label");
    } else {
      await onDeleted();
      close();
    }
  }

  function handleDelete() {
    onActivate();
    setIsConfirming(true);
  }

  function handleConfirm() {
    deleteLabelHandler();
  }

  function handleCancel() {
    setIsConfirming(false);
    close();
  }

  if (isConfirming) {
    return (
      <div className="deleteConfirmation" ref={confirmRef}>
        <p>Are you sure you want to delete this label?</p>
        <div className="confirmButtons">
          <button onClick={handleConfirm} className="confirmButtonDelete">
            Yes, Delete
          </button>
          <button onClick={handleCancel} className="cancelButtonDelete">
            Cancel
          </button>
        </div>
      </div>
    );
  }

  if (!active) return null;

  return (
    <button className="popupOption deleteOption" onClick={handleDelete}>
      Delete
    </button>
  );
}

export default DeleteLabel;
