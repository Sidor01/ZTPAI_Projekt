import './MakeAReservation.css';
import { useNavigate } from "react-router-dom";


export default function MakeAReservation() {
    const navigate = useNavigate();

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
                <img src="/react-app/src/assets/bussiness-man-1.png" alt="UserIcon" className="userIcon"/>
                <h3>Jan Kowalski</h3>
            </div>
            <div className="center-panel">
                <label className="make-a-reservation-label">Choose a meeting date</label>
                <input type="date" id="reservation-date" name="reservation-date" className="input-field"/>
                <label className="make-a-reservation-label">Choose a meeting time</label>
                <input type="time" id="meeting-time" name="meeting-time" className="input-field"/>
                <label className="make-a-reservation-label">Choose a meeting place</label>
                <select id="meeting-place" name="meeting-place" className="select-input-field">
                    <option value="pl. gen. Władysława Sikorskiego 2/2">pl. gen. Władysława Sikorskiego 2/2</option>
                    <option value="Kwiatowa 11">Kwiatowa 11</option>
                    <option value="Długa 33">Długa 33</option>
                </select>
                <button className="accept-reservation-button">Accept</button>
            </div>
        </div>
    );
}