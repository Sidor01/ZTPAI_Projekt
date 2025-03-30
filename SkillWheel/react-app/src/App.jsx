import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Reservations from "./pages/Reservations.jsx";
import Profile from "./pages/Profile";
import { Link } from "react-router-dom";

const App = () => {
    return (
        <Router>
            <nav>
                <Link to="/reservations">Reservations</Link>
                <Link to="/profile">Profile</Link>
            </nav>

            <Routes>
                <Route path="/" element={<Reservations />} />
                <Route path="/reservations" element={<Reservations />} />
                <Route path="/profile" element={<Profile />} />
            </Routes>
        </Router>
    );
};

export default App;

