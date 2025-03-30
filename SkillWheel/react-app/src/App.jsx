import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Reservations from "./pages/Reservations.jsx";
import Profile from "./pages/Profile";
import MakeAReservation from "./pages/MakeAReservation.jsx";
import { Link } from "react-router-dom";

const App = () => {
    return (
        <Router>
            <nav>
                <Link to="/reservations">Reservations</Link>
                <Link to="/profile">Profile</Link>
                <Link to="/make-a-reservation"></Link>
            </nav>

            <Routes>
                <Route path="/" element={<Reservations />} />
                <Route path="/reservations" element={<Reservations />} />
                <Route path="/profile" element={<Profile />} />
                <Route path="/make-a-reservation" element={<MakeAReservation />}/>
            </Routes>
        </Router>
    );
};

export default App;

