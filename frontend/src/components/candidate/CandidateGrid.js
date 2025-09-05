/**
 * CandidateGrid component for displaying multiple candidate cards
 * Provides responsive grid layout for candidate results with sorting
 */

import React, { useMemo } from 'react';
import { SimpleGrid } from '@chakra-ui/react';
import CandidateCard from './CandidateCard';
import { getSortFunction } from '../../utils/sortingUtils';

/**
 * CandidateGrid component
 * @param {Object} props - Component props
 * @param {Array} props.candidates - Array of candidate objects
 * @param {string} props.sortBy - Sort option (default: 'rating-desc')
 * @param {Object} props.gridProps - Additional props for SimpleGrid
 * @returns {JSX.Element} CandidateGrid component
 */
const CandidateGrid = ({ 
    candidates = [], 
    sortBy = 'rating-desc',
    gridProps = {
        columns: { base: 1, md: 2, lg: 3 },
        spacing: 4
    }
}) => {
    // Sort candidates based on the selected sort option
    const sortedCandidates = useMemo(() => {
        if (!candidates || candidates.length === 0) {
            return [];
        }
        
        const sortFunction = getSortFunction(sortBy);
        return sortFunction(candidates);
    }, [candidates, sortBy]);

    if (!sortedCandidates || sortedCandidates.length === 0) {
        return null;
    }

    return (
        <SimpleGrid {...gridProps}>
            {sortedCandidates.map((candidate) => (
                <CandidateCard 
                    key={candidate.filename || candidate.name} 
                    candidate={candidate} 
                />
            ))}
        </SimpleGrid>
    );
};

export default CandidateGrid;
