// src/client/src/index.js
import React from 'react';
import { createRoot } from 'react-dom/client';
import App from './App';
import './styles/global.css';
import { ThemeProvider } from './contexts/ThemeContext';
import { MailProvider } from './contexts/MailContext';


const container = document.getElementById('root');
const root = createRoot(container);
root.render(
  <React.StrictMode>
    <ThemeProvider>
      <MailProvider>
        <App />
      </MailProvider>
    </ThemeProvider>
  </React.StrictMode>
);
