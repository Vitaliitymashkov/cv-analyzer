import React, { useState } from 'react';
import axios from 'axios';

function MatcherPage() {
    const [vacancyDescription, setVacancyDescription] = useState('');
    const [matches, setMatches] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState('');

    const handleMatchCandidates = async () => {
        if (!vacancyDescription.trim()) {
            setError('Please enter a vacancy description.');
            return;
        }
        setIsLoading(true);
        setError('');
        setMatches([]);
        try {
            const response = await axios.post('/api/candidate-matcher/match', vacancyDescription, {
                headers: { 'Content-Type': 'application/json' }
            });
            setMatches(response.data || []);
        } catch (err) {
            console.error("Error while searching for candidates:", err);
            setError('Failed to find candidates.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="section">
            <h2>Candidate Search</h2>
            <textarea
                value={vacancyDescription}
                onChange={(e) => setVacancyDescription(e.target.value)}
                placeholder="Paste the vacancy description here..."
            />
            <button onClick={handleMatchCandidates} disabled={isLoading}>
                {isLoading ? 'Searching...' : 'Find Candidates'}
            </button>
            {isLoading && <div className="loading">Searching for the best candidates...</div>}
            {error && <div className="notification error">{error}</div>}
            <div className="results-container">
                {matches.length > 0 && <h3>Found Candidates:</h3>}
                {matches.map((match, index) => (
                    <div key={index} className="candidate-card">
                        <h3>{match.name}</h3>
                        <p><strong>File: </strong>{match.filename}</p>
                        <p><strong>Rating: </strong>{match.rating}</p>
                        <h4>Analysis Result:</h4>
                        <pre>{match.summary}</pre>
                    </div>
                ))}
            </div>
        </div>
    );
}

export default MatcherPage;