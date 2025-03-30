import { useState } from 'react';
import './Reservations.css';
import { useNavigate } from "react-router-dom";

const reservationsData = [
    {
        id: 1,
        time: "16:00",
        date: "07.03.2025",
        name: "Adam Smith",
        location: "pl. gen. Władysława Sikorskiego 2/2",
        reserved: false
    },
    {id: 2, time: "17:00", date: "07.03.2025", name: "Anna Kowalska", location: "Kwiatowa 11", reserved: true},
    {
        id: 3,
        time: "18:00",
        date: "07.03.2025",
        name: "Adam Smith",
        location: "pl. gen. Władysława Sikorskiego 2/2",
        reserved: false
    },
    {
        id: 4,
        time: "13:30",
        date: "09.03.2025",
        name: "Joanna Nowak",
        location: "pl. gen. Władysława Sikorskiego 2/2",
        reserved: true
    },
    {id: 5, time: "9:15", date: "11.03.2025", name: "Adam Smith", location: "Kwiatowa 11", reserved: false},
];

export default function Reservations() {
    const navigate = useNavigate();
    const [reservations, setReservations] = useState(reservationsData);

    const handleReserve = (id) => {
        setReservations(reservations.map(r => r.id === id ? {...r, reserved: true} : r));
    };

    const handleProfileClick = () => {
        navigate('/profile');
    };

    const handleReservationsClick = () => {
        navigate('/reservations')
    }

    const handleMakeAReservationClick = () => {
        navigate('/make-a-reservation')
    }

    return (
        <div className="container">
            <div className="top-bar">
                <h1> Reservations | Instructor </h1>
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
                    {reservations.map(({id, time, date, name, location, reserved}) => (
                        <div key={id} className="reservation-item">
                            <div className="reservation-details">
                                <div className="reservation-info">
                                    <p className="reservation-time">{time} - {date}</p>
                                    <p className="reservation-name">{name}</p>
                                    <p className="reservation-location">{location}</p>
                                </div>
                                <div className="reservation-action">
                                    {reserved ? (
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