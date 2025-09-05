/**
 * PromptCard component for displaying individual prompt information
 * Provides prompt management interface with edit, view, and reset actions
 */

import React from 'react';
import { 
    Card, 
    CardHeader, 
    CardBody, 
    Heading, 
    HStack, 
    Badge, 
    IconButton, 
    Tooltip, 
    VStack, 
    Text, 
    Divider, 
    Box,
    useColorModeValue 
} from '@chakra-ui/react';
import { EditIcon, RepeatIcon, ViewIcon, CopyIcon } from '@chakra-ui/icons';
import { getPromptTypeColor, getPromptRoleColor, copyToClipboard } from '../../utils/formatters';

/**
 * PromptCard component
 * @param {Object} props - Component props
 * @param {Object} props.prompt - Prompt data object
 * @param {Function} props.onEdit - Edit prompt callback
 * @param {Function} props.onReset - Reset prompt callback
 * @param {Function} props.onCopy - Copy to clipboard callback
 * @returns {JSX.Element} PromptCard component
 */
const PromptCard = ({ prompt, onEdit, onReset, onCopy }) => {
    const cardBg = useColorModeValue('white', 'gray.800');
    const cardBorder = useColorModeValue('gray.200', 'gray.700');
    const previewBg = useColorModeValue('gray.50', 'gray.700');

    const handleCopy = () => {
        copyToClipboard(prompt.content);
        if (onCopy) {
            onCopy('Content copied to clipboard');
        }
    };

    return (
        <Card bg={cardBg} borderColor={cardBorder}>
            <CardHeader>
                <HStack justify="space-between" align="center">
                    <HStack spacing={3}>
                        <Heading size="md" textTransform="capitalize">
                            {prompt.type} - {prompt.role}
                        </Heading>
                        <Badge colorScheme={getPromptTypeColor(prompt.type)}>
                            {prompt.type}
                        </Badge>
                        <Badge colorScheme={getPromptRoleColor(prompt.role)}>
                            {prompt.role}
                        </Badge>
                        {prompt.cached && (
                            <Badge colorScheme="green" variant="outline">
                                Cached
                            </Badge>
                        )}
                    </HStack>
                    <HStack spacing={2}>
                        <Tooltip label="View content">
                            <IconButton
                                icon={<ViewIcon />}
                                size="sm"
                                variant="ghost"
                                onClick={handleCopy}
                            />
                        </Tooltip>
                        <Tooltip label="Edit prompt">
                            <IconButton
                                icon={<EditIcon />}
                                size="sm"
                                variant="ghost"
                                colorScheme="blue"
                                onClick={() => onEdit(prompt)}
                            />
                        </Tooltip>
                        <Tooltip label="Reset to default">
                            <IconButton
                                icon={<RepeatIcon />}
                                size="sm"
                                variant="ghost"
                                colorScheme="orange"
                                onClick={() => onReset(prompt.type, prompt.role)}
                            />
                        </Tooltip>
                    </HStack>
                </HStack>
            </CardHeader>
            <CardBody>
                <VStack align="stretch" spacing={3}>
                    <Text fontSize="sm" color="gray.600">
                        <strong>File Path:</strong> {prompt.filePath}
                    </Text>
                    <Divider />
                    <Box>
                        <Text fontSize="sm" fontWeight="bold" mb={2}>
                            Content Preview:
                        </Text>
                        <Box
                            p={3}
                            bg={previewBg}
                            borderRadius="md"
                            maxH="200px"
                            overflowY="auto"
                            position="relative"
                        >
                            <Text fontSize="sm" whiteSpace="pre-wrap">
                                {prompt.content}
                            </Text>
                            <IconButton
                                icon={<CopyIcon />}
                                size="xs"
                                position="absolute"
                                top={2}
                                right={2}
                                variant="ghost"
                                onClick={handleCopy}
                            />
                        </Box>
                    </Box>
                </VStack>
            </CardBody>
        </Card>
    );
};

export default PromptCard;
