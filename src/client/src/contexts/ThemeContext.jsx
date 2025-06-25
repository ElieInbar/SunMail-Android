import React, { createContext, useLayoutEffect, useState, useCallback, useContext } from 'react';

export const ThemeContext = createContext();

export function ThemeProvider({ children }) {
  
  const [darkMode, setDarkMode] = useState(() => {
    const saved = window.localStorage.getItem('theme') === 'dark';
    
    const root = document.documentElement;
    saved ? root.classList.add('dark-mode')
          : root.classList.remove('dark-mode');
    return saved;
  });

  useLayoutEffect(() => {
    if (darkMode) {
    const frag = document.createDocumentFragment();
    for (let i = 0; i < 60; i++) {
      const s = document.createElement('div');
      s.className = 'star';
      s.style.top  = Math.random() * 80 + 'vh';
      s.style.left = Math.random() * 100 + 'vw';
      s.style.animationDelay = `${Math.random()*3}s`;
      frag.appendChild(s);
    }
    document.body.appendChild(frag);
    return () => { document.querySelectorAll('.star').forEach(el => el.remove()); };
    }
  }, [darkMode]);

  useLayoutEffect(() => {
    const root = document.documentElement;
    if (darkMode) {
      root.classList.add('dark-mode');
      localStorage.setItem('theme', 'dark');
    } else {
      root.classList.remove('dark-mode');
      localStorage.setItem('theme', 'light');
    }
  }, [darkMode]);

  const toggleTheme = useCallback(() => setDarkMode(prev => !prev), []);

  return (
    <ThemeContext.Provider value={{ darkMode, toggleTheme }}>
      {children}
    </ThemeContext.Provider>
  );
}

export const useTheme = () => useContext(ThemeContext);
