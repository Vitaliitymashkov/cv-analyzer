import React from 'react';
import { Link, NavLink } from 'react-router-dom';
import './Navigation.css';

function Navigation() {
    return (
        <nav className="main-nav">
            <ul>
                <li>
                    {/* NavLink adds the 'active' class to the active link */}
                    <NavLink to="/" end>
                        Candidate Search
                    </NavLink>
                </li>
                <li>
                    <NavLink to="/admin">
                        Admin Panel
                    </NavLink>
                </li>
            </ul>
        </nav>
    );
}

export default Navigation;