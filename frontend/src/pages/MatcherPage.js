import React, { useState } from 'react';
import axios from 'axios';
import { Box, Button, Heading, Text, Textarea, SimpleGrid, Spinner, Alert } from '@chakra-ui/react';

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
            const response = await axios.post('/api/candidate-matcher/match', { vacancyDescription }, {
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
        <Box as="section" mb={8}>
            <Box display="flex" alignItems="center" justifyContent="space-between" flexWrap="wrap" gap={4} mb={4}>
                <Heading as="h2" size="lg">Candidate Search</Heading>
                <Button onClick={handleMatchCandidates} isDisabled={isLoading} colorScheme="purple">
                    {isLoading ? 'Searching...' : 'Find Candidates'}
                </Button>
            </Box>
            <Textarea
                value={vacancyDescription}
                onChange={(e) => setVacancyDescription(e.target.value)}
                placeholder="Paste the vacancy description here..."
                minH="160px"
                mb={3}
            />
            {isLoading && (
                <Box display="flex" alignItems="center" gap={3} mb={3} color="gray.400">
                    <Spinner size="sm" />
                    <Text>Searching for the best candidates...</Text>
                </Box>
            )}
            {error && (
                <Alert status="error" mb={3}>
                    {error}
                </Alert>
            )}
            <SimpleGrid columns={{ base: 1, md: 2, lg: 3 }} spacing={4} mt={4}>
                {matches.length > 0 && matches.map((match) => (
                    <Box key={match.filename || match.name} bg="rgba(255,255,255,0.04)" borderWidth="1px" borderColor="rgba(255,255,255,0.12)" borderRadius="md" p={4}>
                        <Heading as="h3" size="md" mb={2}>{match.name}</Heading>
                        <Text fontSize="sm" color="gray.500" mb={1}><strong>File:</strong> {match.filename}</Text>
                        <Text fontSize="sm" color="gray.500" mb={3}><strong>Rating:</strong> {match.rating}</Text>
                        <Heading as="h4" size="sm" mb={2}>Analysis Result</Heading>
                        <Box as="pre" whiteSpace="pre-wrap" wordBreak="break-word" bg="rgba(0,0,0,0.25)" p={3} borderRadius="sm" fontSize="sm" color="inherit">
                            {match.summary}
                        </Box>
                    </Box>
                ))}
            </SimpleGrid>
        </Box>
    );
}

export default MatcherPage;