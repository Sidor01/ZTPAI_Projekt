import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './LoginPage.css';

const LoginPage = () => {
    const [formData, setFormData] = useState({
        username: '',
        password: ''
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');

        try {
            const response = await axios.post(
                'http://localhost:8080/api/auth/login',
                {
                    username: formData.username,
                    password: formData.password
                }
            );

            localStorage.setItem('jwtToken', response.data.token);
            navigate('/dashboard');
        } catch (err) {
            if (err.response) {
                setError(err.response.data.error || 'Login failed');
            } else {
                setError('Network error - could not connect to server');
            }
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-page-wrapper">
            <div className="login-container">
                <div className="login-box">
                    <h2 className="login-title">SkillWheel Login</h2>
                    {error && <div className="error-message">{error}</div>}

                    <form className="login-form" onSubmit={handleSubmit}>
                        <div className="input-group">
                            <label htmlFor="username">Email:</label>
                            <input
                                id="username"
                                type="email"
                                name="username"
                                value={formData.username}
                                onChange={handleChange}
                                required
                            />
                        </div>

                        <div className="input-group">
                            <label htmlFor="password">Password:</label>
                            <input
                                id="password"
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                required
                                minLength="8"
                            />
                        </div>

                        <button
                            type="submit"
                            className="login-button"
                            disabled={loading}
                        >
                            {loading ? 'Logging in...' : 'Login'}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default LoginPage;