import React, { useState } from 'react';
import axios from 'axios';

function AdminPage() {
    const [notification, setNotification] = useState({ message: '', type: '' });
    const [isLoading, setIsLoading] = useState(false);

    const handleRefreshPrompts = async () => {
        setIsLoading(true);
        setNotification({ message: '', type: '' });
        try {
            await axios.post('/api/admin/prompts/refresh', null);
            setNotification({ message: 'Prompts updated successfully!', type: 'success' });
        } catch (error) {
            console.error("Error while updating prompts:", error);
            setNotification({ message: 'Failed to update prompts.', type: 'error' });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="section">
            <h2>Admin Panel</h2>
            <button onClick={handleRefreshPrompts} disabled={isLoading}>
                {isLoading ? 'Updating...' : 'Update Prompts'}
            </button>
            {notification.message && (
                <div className={`notification ${notification.type}`}>
                    {notification.message}
                </div>
            )}
        </div>
    );
}

export default AdminPage;