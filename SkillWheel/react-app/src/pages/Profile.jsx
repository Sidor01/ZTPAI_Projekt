import { useState, useEffect } from 'react';
import './Profile.css';
import { useNavigate } from "react-router-dom";
import axios from 'axios';

export default function Profile() {
    const navigate = useNavigate();

    const [name, setName] = useState("");
    const [surname, setSurname] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [schoolName, setSchoolName] = useState("");
    const [userId, setUserId] = useState(1);

    useEffect(() => {
        // Pobierz dane użytkownika o ID 1
        axios.get(`/api/students/${userId}`)
            .then(response => {
                const student = response.data;
                setName(student.name);
                setSurname(student.surname);
                setEmail(student.email);
                setPassword(student.password);
                setSchoolName(student.nameOfSchool);
            })
            .catch(error => {
                console.error('Error fetching student data:', error);
            });
    }, [userId]);

    const handleSaveClick = () => {
        const updatedStudent = {
            name,
            surname,
            email,
            password,
            nameOfSchool: schoolName
        };

        axios.put(`/api/students/${userId}`, updatedStudent)
            .then(response => {
                console.log('Student data updated successfully', response.data);
                alert('Profile updated successfully');
            })
            .catch(error => {
                console.error('Error updating student data:', error);
                alert('Failed to update profile');
            });
    };

    const handleReservationsClick = () => {
        navigate('/reservations')
    }

    const handleProfileClick = () => {
        navigate('/profile')
    }

    const handleMakeAReservationClick = () => {
        navigate('/make-a-reservation')
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
                <button className="save-profile-button" onClick={handleSaveClick}>Save</button>
            </div>

            <div className="top-bar">
                <h1> Profile | Instructor </h1>
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
        </div>
    );
}