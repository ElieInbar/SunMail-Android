import "../css/LabelMenu.css";
import { useState, useEffect, useRef } from "react";
import Label from "./Label";


function LabelMenu({labels, fetchLabels}) {
  const [activePopup, setActivePopup] = useState(null);
  const popupRef = useRef(null);

  const lastLabelRef = useRef(null);
  useEffect(() => {
    if (labels.length && lastLabelRef.current) {
      lastLabelRef.current.scrollIntoView({ behavior: "smooth", block: "nearest" });
    }
  }, [labels]);

  // Fermer le popup en cliquant à l'extérieur
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
    fetchLabels();
  }, []);

  return (
    <div>
  <ul className="Labels">
    {labels.map((label, idx) => {
      if (label.name === "spam"||label.name==="inbox") return null;
      return (
        <li key={label.id} ref={idx===labels.length - 1 ? lastLabelRef : null}>
          <Label label={label} fetchLabels={fetchLabels} />
        </li>
      );
    })}
  </ul>
</div>

  );
}

export default LabelMenu;
