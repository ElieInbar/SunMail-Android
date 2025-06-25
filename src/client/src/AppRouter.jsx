// src/AppRouter.jsx
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import InboxPage from "./pages/jsx/InboxPage";
import LoginPage from "./pages/jsx/LoginPage";
import RegisterPage from "./pages/jsx/RegisterPage";
import Inbox from "./components/jsx/Inbox";
import SearchResultsPage from "./pages/jsx/SearchResultsPage";
import MailRenderer from "./components/jsx/MailRenderer";
import { AuthProvider, AuthContext } from "./contexts/AuthContext";
import { useContext, useState, useEffect } from "react";

// Protected Route component
function ProtectedRoute({ children }) {
  const { isLoggedIn } = useContext(AuthContext);
  return isLoggedIn ? children : <Navigate to="/login" replace />;
}

function HomeRedirect() {
  const { isLoggedIn, username, logout} = useContext(AuthContext);
  const [loading, setLoading] = useState(true);
  const [validUser, setValidUser] = useState(false);

  useEffect(() => {
    if (!isLoggedIn || !username) {
      setLoading(false);
      return;
    }

    fetch(`/api/users/by-username/${username}`)
      .then((res) => {
        if (!res.ok) throw new Error("User not found");
        return res.json();
      })
      .then(() => setValidUser(true))
      .catch(() => {
        logout();
      })
      .finally(() => setLoading(false));
  }, [isLoggedIn, username, logout]);

  if (loading) return null;

  return validUser ? (
    <Navigate to="/inbox" replace />
  ) : (
    <Navigate to="/login" replace />
  );
}

function AppRoutes() {
  const { userChecked } = useContext(AuthContext);

  // Don't render anything until user check is done
  if (!userChecked) {
    return null; // or <div>Loading...</div>
  }

  return (
    <Routes>
      <Route path="/" element={<HomeRedirect />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />
      <Route
        path="/:label"
        element={
          <ProtectedRoute>
            <InboxPage />
          </ProtectedRoute>
        }
      >
        <Route index element={<Inbox />} />
         <Route path=":id" element={<MailRenderer />} />
      </Route>
      <Route path="/search" element={<ProtectedRoute><InboxPage /></ProtectedRoute>}>
        <Route index element={<SearchResultsPage />} />
      </Route>
      {/* Add other protected routes here */}
    </Routes>
  );
}

export default function AppRouter() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  );
}
