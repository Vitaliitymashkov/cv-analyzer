import React, { useState } from 'react';
import { Box, Button, Heading, Textarea, Select, HStack, Text, VStack } from '@chakra-ui/react';
import { useCandidateMatching } from '../hooks/useApiCall';
import CandidateGrid from '../components/candidate/CandidateGrid';
import LoadingSpinner from '../components/common/LoadingSpinner';
import EnhancedErrorAlert from '../components/common/EnhancedErrorAlert';
import { SORT_OPTIONS } from '../utils/sortingUtils';

function MatcherPage() {
    const [vacancyDescription, setVacancyDescription] = useState('');
    const [sortBy, setSortBy] = useState('rating-desc');
    const [errorKey, setErrorKey] = useState(0);
    const { matches, loading, error, matchCandidates, setError } = useCandidateMatching();

    const handleMatchCandidates = () => {
        // Clear ALL errors immediately when user clicks Find Candidates
        setError(null);
        // Force error alert to re-render by changing its key
        setErrorKey(prev => prev + 1);
        // The matchCandidates function will also clear errors automatically
        matchCandidates(vacancyDescription);
    };

    const handleDescriptionChange = (e) => {
        const value = e.target.value;
        setVacancyDescription(value);
        
        // Don't clear errors when typing - only when user clicks Find Candidates
    };


    return (
        <Box as="section" mb={8}>
            <Box display="flex" alignItems="center" justifyContent="space-between" flexWrap="wrap" gap={4} mb={4}>
                <Heading as="h2" size="lg">Candidate Search</Heading>
                <HStack spacing={2}>
                    <Button onClick={handleMatchCandidates} isDisabled={loading} colorScheme="purple">
                        {loading ? 'Searching...' : 'Find Candidates'}
                    </Button>
                </HStack>
            </Box>
            
            
            <Textarea
                value={vacancyDescription}
                onChange={handleDescriptionChange}
                placeholder="Paste the vacancy description here..."
                minH="160px"
                mb={3}
            />
            
            {loading && (
                <LoadingSpinner 
                    message="Searching for the best candidates..." 
                    size="sm"
                    containerProps={{ 
                        display: "flex", 
                        alignItems: "center", 
                        gap: 3, 
                        mb: 3, 
                        color: "gray.400",
                        minH: "auto"
                    }}
                />
            )}
            
            <EnhancedErrorAlert 
                key={errorKey}
                error={error} 
                showDetails={true}
                containerProps={{ mb: 3 }} 
            />
            
            {matches && matches.length > 0 && (
                <VStack spacing={4} align="stretch" mb={6}>
                    <HStack justify="space-between" align="center" wrap="wrap" gap={2}>
                        <Text fontSize="sm" color="gray.600" fontWeight="medium">
                            Sort candidates by:
                        </Text>
                        <Select
                            value={sortBy}
                            onChange={(e) => setSortBy(e.target.value)}
                            size="sm"
                            maxW="200px"
                            bg="white"
                            _dark={{ bg: "gray.700" }}
                        >
                            {Object.values(SORT_OPTIONS).map((option) => (
                                <option key={option.value} value={option.value}>
                                    {option.label}
                                </option>
                            ))}
                        </Select>
                    </HStack>
                </VStack>
            )}
            
            <CandidateGrid candidates={matches} sortBy={sortBy} />
        </Box>
    );
}

export default MatcherPage;