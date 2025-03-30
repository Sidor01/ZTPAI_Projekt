import {use, useEffect, useState} from 'react';
import './Profile.css';
import {useNavigate} from "react-router-dom";

export default function Reservations() {

    const navigate = useNavigate();

    const handleReservationsClick = () => {
        navigate('/reservations')
    }

    const handleProfileClick = () => {
        navigate('/profile')
    }

    return (
        <div className="container">
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
