/**
 * Enhanced ErrorAlert component for displaying different types of errors
 * Provides informative error messages without retry functionality
 */

import React, { useState, useEffect } from 'react';
import { 
    Alert, 
    AlertIcon, 
    AlertTitle, 
    AlertDescription, 
    Box, 
    VStack, 
    HStack, 
    Text, 
    Collapse,
    IconButton,
    Tooltip
} from '@chakra-ui/react';
import { CloseIcon, InfoIcon } from '@chakra-ui/icons';
import { parseError, getUserFriendlyMessage } from '../../utils/errorHandler';

/**
 * Enhanced ErrorAlert component
 * @param {Object} props - Component props
 * @param {Object} props.error - Error object (Axios error or parsed error)
 * @param {Function} props.onDismiss - Dismiss callback function
 * @param {boolean} props.showDetails - Whether to show detailed error information
 * @param {Object} props.containerProps - Additional props for container
 * @returns {JSX.Element} EnhancedErrorAlert component
 */
const EnhancedErrorAlert = ({ 
    error, 
    onDismiss, 
    showDetails = false,
    containerProps = {}
}) => {
    const [parsedError, setParsedError] = useState(null);
    const [isExpanded, setIsExpanded] = useState(false);

    useEffect(() => {
        if (error) {
            const parsed = parseError(error);
            setParsedError(parsed);
        }
    }, [error]);

    if (!parsedError) return null;

    const { type, title, action, severity = 'error' } = parsedError;
    const userMessage = getUserFriendlyMessage(parsedError);

    const getStatusColor = () => {
        switch (severity) {
            case 'warning': return 'warning';
            case 'error': return 'error';
            default: return 'error';
        }
    };

    const getStatusIcon = () => {
        switch (type) {
            case 'RATE_LIMIT': return '⏱️';
            case 'TIMEOUT': return '⏰';
            case 'NETWORK_ERROR': return '🌐';
            case 'VALIDATION': return '⚠️';
            default: return '❌';
        }
    };

    const handleDismiss = () => {
        if (onDismiss) {
            onDismiss();
        }
    };

    return (
        <Alert 
            status={getStatusColor()} 
            variant="left-accent"
            {...containerProps}
        >
            <AlertIcon />
            <Box flex="1">
                <VStack align="start" spacing={2}>
                    <HStack justify="space-between" width="100%">
                        <HStack spacing={2}>
                            <Text fontSize="lg">{getStatusIcon()}</Text>
                            <AlertTitle fontSize="md">{title}</AlertTitle>
                        </HStack>
                        
                        <HStack spacing={2}>
                            {showDetails && (
                                <Tooltip label={isExpanded ? "Hide details" : "Show details"}>
                                    <IconButton
                                        size="sm"
                                        variant="ghost"
                                        icon={<InfoIcon />}
                                        onClick={() => setIsExpanded(!isExpanded)}
                                        aria-label="Toggle details"
                                    />
                                </Tooltip>
                            )}
                            
                            {onDismiss && (
                                <IconButton
                                    size="sm"
                                    variant="ghost"
                                    icon={<CloseIcon />}
                                    onClick={handleDismiss}
                                    aria-label="Dismiss error"
                                />
                            )}
                        </HStack>
                    </HStack>

                    <AlertDescription fontSize="sm">
                        {userMessage}
                    </AlertDescription>

                    {action && (
                        <Text fontSize="xs" color="gray.600" fontStyle="italic">
                            💡 {action}
                        </Text>
                    )}

                    {/* Detailed error information */}
                    <Collapse in={isExpanded} animateOpacity>
                        <Box width="100%" p={2} bg="gray.50" borderRadius="md" _dark={{ bg: "gray.700" }}>
                            <VStack align="start" spacing={1}>
                                <Text fontSize="xs" fontWeight="bold">Error Details:</Text>
                                <Text fontSize="xs">Type: {type}</Text>
                                <Text fontSize="xs">Severity: {severity}</Text>
                            </VStack>
                        </Box>
                    </Collapse>
                </VStack>
            </Box>
        </Alert>
    );
};

export default EnhancedErrorAlert;
