/**
 * LoadingSpinner component for displaying loading states
 * Provides consistent loading UI across the application
 */

import React from 'react';
import { Box, Spinner, Text, VStack } from '@chakra-ui/react';

/**
 * LoadingSpinner component
 * @param {Object} props - Component props
 * @param {string} props.message - Loading message
 * @param {string} props.size - Spinner size
 * @param {Object} props.containerProps - Additional props for container
 * @returns {JSX.Element} LoadingSpinner component
 */
const LoadingSpinner = ({ 
    message = 'Loading...', 
    size = 'md',
    containerProps = {}
}) => {
    return (
        <Box 
            display="flex" 
            alignItems="center" 
            justifyContent="center" 
            minH="200px"
            {...containerProps}
        >
            <VStack spacing={4}>
                <Spinner size={size} />
                <Text color="gray.500">{message}</Text>
            </VStack>
        </Box>
    );
};

export default LoadingSpinner;
