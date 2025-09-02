import React, { useState } from 'react';
import axios from 'axios';
import { Box, Button, Heading, Alert } from '@chakra-ui/react';

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
        <Box as="section" mb={8}>
            <Box display="flex" alignItems="center" justifyContent="space-between" flexWrap="wrap" gap={4} mb={4}>
                <Heading as="h2" size="lg">Admin Panel</Heading>
                <Button onClick={handleRefreshPrompts} isDisabled={isLoading} colorScheme="purple">
                    {isLoading ? 'Updating...' : 'Update Prompts'}
                </Button>
            </Box>
            {notification.message && (
                <Alert status={notification.type === 'success' ? 'success' : 'error'}>
                    {notification.message}
                </Alert>
            )}
        </Box>
    );
}

export default AdminPage;