// MailContext.jsx
import { createContext, useState, useEffect } from "react";

export const MailContext = createContext();

export function MailProvider({ children }) {
    const [draftId, setDraftId] = useState(null);
    const [isComposeOpen, setIsComposeOpen] = useState(false);
    const [isMinimized, setIsMinimized] = useState(false);
    const [typeOfDraft, setTypeOfDraft] = useState(null);
    const [currentFolder, setCurrentFolder] = useState("inbox");
    const [mails, setMails] = useState([]);
    const [allMails, setAllMails] = useState([]);
    const [formData, setFormData] = useState({
        to: "",
        subject: "",
        body: "",
    });

    async function fetchAllMails() {
        const res = await fetch("/api/mails", { credentials: "include" });
        if (res.ok) {
            setAllMails(await res.json());
        }
    }
    
    async function fetchMails(currentFolder) {
        var res;
        res = await fetch(`/api/mails/label/${currentFolder}`, {
            credentials: "include",
        });
        const json = await res.json();
        setMails(json);
    }


    return (
        <MailContext.Provider
            value={{
                allMails,
                fetchAllMails,
                draftId,
                setDraftId,
                isComposeOpen,
                setIsComposeOpen,
                isMinimized,
                setIsMinimized,
                typeOfDraft,
                setTypeOfDraft,
                currentFolder,
                setCurrentFolder,
                fetchMails,
                mails,
                setMails,
                formData,
                setFormData
            }}
        >
            {children}
        </MailContext.Provider>
    );
}
