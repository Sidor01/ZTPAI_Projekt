import { useState, useEffect } from 'react';
import './Reservations.css';
import { useNavigate } from "react-router-dom";

export default function Reservations() {
    const navigate = useNavigate();
    const [reservations, setReservations] = useState([]);
    const [instructor, setInstructor] = useState({ name: '', surname: '' });

    useEffect(() => {
        // Fetch reservations
        fetch('/api/reservations/instructor/1')
            .then(response => response.json())
            .then(data => {
                if (data.status === 200) {
                    setReservations(data.reservations);
                } else {
                    console.error('Failed to fetch reservations:', data.error);
                }
            })
            .catch(error => console.error('Error fetching reservations:', error));

        // Fetch instructor details
        fetch('/api/instructors/1')
            .then(response => response.json())
            .then(data => {
                if (data.status === 200) {
                    setInstructor(data.instructor);
                } else {
                    console.error('Failed to fetch instructor:', data.error);
                }
            })
            .catch(error => console.error('Error fetching instructor:', error));
    }, []);

    const handleReserve = (id) => {
        setReservations(reservations.map(r => r.id === id ? {...r, reserved: true} : r));
    };

    const handleProfileClick = () => {
        navigate('/profile');
    };

    const handleReservationsClick = () => {
        navigate('/reservations');
    };

    const handleMakeAReservationClick = () => {
        navigate('/make-a-reservation');
    };

    return (
        <div className="container">
            <div className="top-bar">
                <h1>Reservations | Instructor</h1>
            </div>
            <div className="left-bar">
                <button className="profile" onClick={handleProfileClick}>
                    Profile
                </button>
                <button className="make-a-reservation" onClick={handleMakeAReservationClick}>
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
            <div className="reservation-box">
                <h2>Make a reservation</h2>
                <div className="reservation-list">
                    {reservations.map(({id, reservationTime, reservationDate, reservationPlace, isReserved}) => (
                        <div key={id} className="reservation-item">
                            <div className="reservation-details">
                                <div className="reservation-info">
                                    <p className="reservation-time">{reservationTime} - {reservationDate}</p>
                                    <p className="reservation-name">{instructor.name} {instructor.surname}</p>
                                    <p className="reservation-location">{reservationPlace}</p>
                                </div>
                                <div className="reservation-action">
                                    {isReserved ? (
                                        <button className="reserved-button" disabled>
                                            Reserved
                                        </button>
                                    ) : (
                                        <button className="reserve-button" onClick={() => handleReserve(id)}>
                                            Reserve
                                        </button>
                                    )}
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}