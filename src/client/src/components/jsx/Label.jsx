import "../css/Label.css";
import EditLabel from "./EditLabel";
import DeleteLabel from "./DeleteLabel";
import { useState, useEffect, useRef, useContext } from "react";
import { MailContext } from "../../contexts/MailContext";
import { useNavigate } from "react-router-dom";

function Label({ label, fetchLabels }) {
  const [activePopup, setActivePopup] = useState(null);
  const [activeOption, setActiveOption] = useState(null);
  const { setCurrentFolder } = useContext(MailContext);
  const popupRef = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    function handleClickOutside(event) {
      if (popupRef.current && !popupRef.current.contains(event.target)) {
        setActivePopup(null);
      }
    }

    if (activePopup) {
      document.addEventListener("mousedown", handleClickOutside);
      return () => {
        document.removeEventListener("mousedown", handleClickOutside);
      };
    }
  }, [activePopup]);

  useEffect(() => {
    if (activePopup && popupRef.current) {
      popupRef.current.scrollIntoView({ behavior: "smooth", block: "nearest" });
    }
  }, [activePopup]);


  function handleLabelClick(name) {
    setCurrentFolder(name)
    navigate(`../${name}`)
  }

  return (
    <div className="label" onClick={() => handleLabelClick(label.name)} tabIndex={0} >
      <span className="labelName">{label.name}</span>
      <span className="labelIcon">
        <button
          className="dots"
          onClick={(e) => {
            e.stopPropagation();
            setActivePopup(activePopup === label.id ? null : label.id);
            setActiveOption("both");
          }}
        >
          <i className="bi bi-three-dots-vertical"></i>
        </button>
        {activePopup === label.id && (
          <div
          className="popupMenu"
          ref={popupRef}    
          onMouseLeave={() => {       
            setActivePopup(null);
            setActiveOption(null);
          }}
        >
            <EditLabel
              label={label}
              onUpdated={() => {
                fetchLabels();
                setActiveOption(null);
              }}
              close={() => {
                setActivePopup(null);
                setActiveOption(null);
              }}
              onActivate={() => setActiveOption("edit")}
              active={activeOption === "edit" || activeOption === "both"}
            />
            <DeleteLabel
              labelId={label.id}
              onDeleted={() => {
                fetchLabels();
                setActiveOption(null);
              }}
              close={() => {
                setActivePopup(null);
                setActiveOption(null);
              }}
              onActivate={() => setActiveOption("delete")}
              active={activeOption === "delete" || activeOption === "both"}
            />
          </div>
        )}
      </span>
    </div>
  );
}

export default Label;
