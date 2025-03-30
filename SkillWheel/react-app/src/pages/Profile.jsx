import { useState } from 'react';
import './Profile.css';
import { useNavigate } from "react-router-dom";

export default function Profile() {
    const navigate = useNavigate();

    const [name, setName] = useState("Jan");
    const [surname, setSurname] = useState("Kowalski");
    const [email, setEmail] = useState("jan.kowalski@example.com");
    const [password, setPassword] = useState("password123");
    const [schoolName] = useState("Example School");

    const handleReservationsClick = () => {
        navigate('/reservations')
    }

    const handleProfileClick = () => {
        navigate('/profile')
    }

    return (
        <div className="container">
            <div className="center-panel">
                <input
                    type="text"
                    placeholder="Name"
                    className="input-field"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                />
                <input
                    type="text"
                    placeholder="Surname"
                    className="input-field"
                    value={surname}
                    onChange={(e) => setSurname(e.target.value)}
                />
                <input
                    type="email"
                    placeholder="E-mail"
                    className="input-field"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                />
                <input
                    type="password"
                    placeholder="Password"
                    className="input-field"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                />
                <input
                    type="text"
                    placeholder="School name"
                    className="input-field"
                    value={schoolName}
                    disabled
                />
                <button className="save-profile-button">Save</button>
            </div>

            <div className="top-bar">
                <h1> Profile | Student </h1>
            </div>
            <div className="left-bar">
                <button className="profile" onClick={handleProfileClick}>
                    Profile
                </button>
                <button className="make-a-reservation">
                    Make a reservation
                </button>
                <button className="reservations" onClick={handleReservationsClick}>
                    Reservations
                </button>
            </div>
            <div className="intersection-box">
                <img src="/react-app/src/assets/bussiness-man-1.png" alt="UserIcon" className="userIcon"/>
                <h3>Jan Kowalski</h3>
            </div>
        </div>
    );
}