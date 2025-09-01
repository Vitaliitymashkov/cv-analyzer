import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css';
import Navigation from './components/Navigation';
import MatcherPage from './pages/MatcherPage';
import AdminPage from './pages/AdminPage';

function App() {
    return (
        <BrowserRouter>
            <div className="App">
                <header>
                    <h1>CV Analyzer</h1>
                    <Navigation />
                </header>
                <main>
                    <Routes>
                        <Route path="/" element={<MatcherPage />} />
                        
                        <Route path="/admin" element={<AdminPage />} />
                    </Routes>
                </main>
            </div>
        </BrowserRouter>
    );
}

export default App;