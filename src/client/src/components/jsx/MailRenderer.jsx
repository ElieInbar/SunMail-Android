// src/components/jsx/MailRenderer.jsx
import { useParams } from "react-router-dom";
import ComposeWindow from "./ComposeWindow";
import MailPage from "../../pages/jsx/MailPage";
import { MailContext } from "../../contexts/MailContext";
import { useContext, useEffect } from "react";

export default function MailRenderer() {
    const { label } = useParams();
    const { setTypeOfDraft, setIsComposeOpen, setIsMinimized } = useContext(MailContext);

    useEffect(() => {
        if (label === "drafts") {
            setIsComposeOpen(true);
            setIsMinimized(false); 
            setTypeOfDraft(null);
        }
    }, [label, setIsComposeOpen, setIsMinimized]);

    if (label === "drafts") {
        return <ComposeWindow />;
    }

    return <MailPage />;
}
