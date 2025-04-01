import './MakeAReservation.css';
import { useNavigate } from "react-router-dom";
import { useState } from 'react';

export default function MakeAReservation() {
    const navigate = useNavigate();
    const [reservationDate, setReservationDate] = useState('');
    const [reservationTime, setReservationTime] = useState('');
    const [reservationPlace, setReservationPlace] = useState('');

    const handleProfileClick = () => {
        navigate('/profile');
    };

    const handleReservationsClick = () => {
        navigate('/reservations')
    }

    const handleMakeAReservationClick = () => {
        navigate('/make-a-reservation')
    }

    const handleAcceptClick = async () => {
        const reservation = {
            isReserved: false,
            studentID: 1, // Replace with actual student ID
            instructorID: 1, // Replace with actual instructor ID
            reservationDate: reservationDate,
            reservationTime: reservationTime,
            reservationPlace: reservationPlace,
        };

        try {
            const response = await fetch('/api/reservations', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(reservation),
            });

            if (response.ok) {
                alert('Reservation made successfully');
                navigate('/reservations');
            } else {
                alert('Failed to make reservation');
            }
        } catch (error) {
            console.error('Error:', error);
            alert('An error occurred while making the reservation');
        }
    };

    return (
        <div className="container">
            <div className="top-bar">
                <h1> Make a reservation | Instructor </h1>
            </div>
            <div className="left-bar">
                <button className="profile" onClick={handleProfileClick}>
                    Profile
                </button>
                <button className="reservations" onClick={handleReservationsClick}>
                    Reservations
                </button>
                <button className="make-a-reservation" onClick={handleMakeAReservationClick}>
                    Make a reservation
                </button>
            </div>
            <div className="intersection-box">
                <img src="/assets/bussiness-man-2.png" alt="UserIcon" className="userIcon"/>
                <h3>Jan Kowalski</h3>
            </div>
            <div className="center-panel">
                <label className="make-a-reservation-label">Choose a meeting date</label>
                <input type="date" id="reservation-date" name="reservation-date" className="input-field" value={reservationDate} onChange={(e) => setReservationDate(e.target.value)}/>
                <label className="make-a-reservation-label">Choose a meeting time</label>
                <input type="time" id="meeting-time" name="meeting-time" className="input-field" value={reservationTime} onChange={(e) => setReservationTime(e.target.value)}/>
                <label className="make-a-reservation-label">Choose a meeting place</label>
                <select id="meeting-place" name="meeting-place" className="select-input-field" value={reservationPlace} onChange={(e) => setReservationPlace(e.target.value)}>
                    <option value="Sikorskiego 2">Sikorskiego 2</option>
                    <option value="Kwiatowa 11">Kwiatowa 11</option>
                    <option value="Długa 33">Długa 33</option>
                </select>
                <button className="accept-reservation-button" onClick={handleAcceptClick}>Accept</button>
            </div>
        </div>
    );
}