import { useState } from 'react';
import ComposeWindow from './ComposeWindow';
import '../css/ComposeMail.css';

export default function ComposeMailButton() {
  const [isComposeOpen, setIsComposeOpen] = useState(false);
  const [isMinimized, setIsMinimized] = useState(false);

  const handleOpenCompose = () => {
    setIsComposeOpen(true);
    setIsMinimized(false);
  };

  const handleCloseCompose = () => {
    setIsComposeOpen(false);
    setIsMinimized(false);
  };

  const handleMinimizeCompose = () => {
    setIsMinimized(!isMinimized);
  };

  return (
    <>
      <button className="compose-mail-btn" onClick={handleOpenCompose}>
        + New Message
      </button>

      <ComposeWindow
        isOpen={isComposeOpen}
        onClose={handleCloseCompose}
        onMinimize={handleMinimizeCompose}
        isMinimized={isMinimized}
      />
    </>
  );
}
