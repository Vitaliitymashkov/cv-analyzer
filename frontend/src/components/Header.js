import React from 'react';
import logo from '../assets/logo.png'; // Adjust path if needed
import './Header.css';

function Header() {
    return (
        <header className="app-header">
            <img src={logo} alt="Logo" className="logo" />
            <h1>CV Analyzer</h1>
        </header>
    );
}

export default Header;