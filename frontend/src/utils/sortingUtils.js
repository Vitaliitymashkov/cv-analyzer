/**
 * Sorting utility functions for candidate data
 * Provides various sorting options for candidate lists
 */

/**
 * Sort candidates by rating from best (highest) to worst (lowest)
 * @param {Array} candidates - Array of candidate objects
 * @param {string} order - Sort order: 'desc' (best first) or 'asc' (worst first)
 * @returns {Array} Sorted array of candidates
 */
export const sortCandidatesByRating = (candidates, order = 'desc') => {
    if (!Array.isArray(candidates) || candidates.length === 0) {
        return candidates;
    }

    return [...candidates].sort((a, b) => {
        const ratingA = parseFloat(a.rating) || 0;
        const ratingB = parseFloat(b.rating) || 0;
        
        if (order === 'desc') {
            return ratingB - ratingA; // Highest first
        } else {
            return ratingA - ratingB; // Lowest first
        }
    });
};

/**
 * Sort candidates by name alphabetically
 * @param {Array} candidates - Array of candidate objects
 * @param {string} order - Sort order: 'asc' (A-Z) or 'desc' (Z-A)
 * @returns {Array} Sorted array of candidates
 */
export const sortCandidatesByName = (candidates, order = 'asc') => {
    if (!Array.isArray(candidates) || candidates.length === 0) {
        return candidates;
    }

    return [...candidates].sort((a, b) => {
        const nameA = (a.name || '').toLowerCase();
        const nameB = (b.name || '').toLowerCase();
        
        if (order === 'asc') {
            return nameA.localeCompare(nameB);
        } else {
            return nameB.localeCompare(nameA);
        }
    });
};

/**
 * Sort candidates by filename alphabetically
 * @param {Array} candidates - Array of candidate objects
 * @param {string} order - Sort order: 'asc' (A-Z) or 'desc' (Z-A)
 * @returns {Array} Sorted array of candidates
 */
export const sortCandidatesByFilename = (candidates, order = 'asc') => {
    if (!Array.isArray(candidates) || candidates.length === 0) {
        return candidates;
    }

    return [...candidates].sort((a, b) => {
        const filenameA = (a.filename || '').toLowerCase();
        const filenameB = (b.filename || '').toLowerCase();
        
        if (order === 'asc') {
            return filenameA.localeCompare(filenameB);
        } else {
            return filenameB.localeCompare(filenameA);
        }
    });
};

/**
 * Sort candidates by rating percentage (normalized score)
 * @param {Array} candidates - Array of candidate objects
 * @param {string} order - Sort order: 'desc' (highest percentage first) or 'asc' (lowest percentage first)
 * @returns {Array} Sorted array of candidates
 */
export const sortCandidatesByPercentage = (candidates, order = 'desc') => {
    if (!Array.isArray(candidates) || candidates.length === 0) {
        return candidates;
    }

    return [...candidates].sort((a, b) => {
        const percentageA = calculateRatingPercentage(a);
        const percentageB = calculateRatingPercentage(b);
        
        if (order === 'desc') {
            return percentageB - percentageA; // Highest percentage first
        } else {
            return percentageA - percentageB; // Lowest percentage first
        }
    });
};

/**
 * Calculate rating percentage based on min/max rating
 * @param {Object} candidate - Candidate object
 * @returns {number} Rating percentage (0-100)
 */
const calculateRatingPercentage = (candidate) => {
    const rating = parseFloat(candidate.rating) || 0;
    const minRating = parseFloat(candidate.minRating) || 0;
    const maxRating = parseFloat(candidate.maxRating) || 100;
    
    if (maxRating === minRating) {
        return 0;
    }
    
    return ((rating - minRating) / (maxRating - minRating)) * 100;
};

/**
 * Sort candidates with multiple criteria
 * @param {Array} candidates - Array of candidate objects
 * @param {Array} sortCriteria - Array of sort criteria objects
 * @returns {Array} Sorted array of candidates
 */
export const sortCandidatesMultiCriteria = (candidates, sortCriteria = []) => {
    if (!Array.isArray(candidates) || candidates.length === 0) {
        return candidates;
    }

    if (!Array.isArray(sortCriteria) || sortCriteria.length === 0) {
        return sortCandidatesByRating(candidates, 'desc'); // Default to rating desc
    }

    return [...candidates].sort((a, b) => {
        for (const criterion of sortCriteria) {
            const { field, order = 'desc' } = criterion;
            let comparison = 0;

            switch (field) {
                case 'rating':
                    comparison = (parseFloat(a.rating) || 0) - (parseFloat(b.rating) || 0);
                    break;
                case 'name':
                    comparison = (a.name || '').localeCompare(b.name || '');
                    break;
                case 'filename':
                    comparison = (a.filename || '').localeCompare(b.filename || '');
                    break;
                case 'percentage':
                    comparison = calculateRatingPercentage(a) - calculateRatingPercentage(b);
                    break;
                default:
                    continue;
            }

            if (comparison !== 0) {
                return order === 'desc' ? -comparison : comparison;
            }
        }
        return 0;
    });
};

/**
 * Sort options for UI components
 */
export const SORT_OPTIONS = {
    RATING_DESC: { value: 'rating-desc', label: 'Best Match First', field: 'rating', order: 'desc' },
    RATING_ASC: { value: 'rating-asc', label: 'Worst Match First', field: 'rating', order: 'asc' },
    NAME_ASC: { value: 'name-asc', label: 'Name A-Z', field: 'name', order: 'asc' },
    NAME_DESC: { value: 'name-desc', label: 'Name Z-A', field: 'name', order: 'desc' },
    FILENAME_ASC: { value: 'filename-asc', label: 'Filename A-Z', field: 'filename', order: 'asc' },
    FILENAME_DESC: { value: 'filename-desc', label: 'Filename Z-A', field: 'filename', order: 'desc' },
    PERCENTAGE_DESC: { value: 'percentage-desc', label: 'Highest Percentage', field: 'percentage', order: 'desc' },
    PERCENTAGE_ASC: { value: 'percentage-asc', label: 'Lowest Percentage', field: 'percentage', order: 'asc' }
};

/**
 * Get sort function based on sort option
 * @param {string} sortOption - Sort option value
 * @returns {Function} Sort function
 */
export const getSortFunction = (sortOption) => {
    switch (sortOption) {
        case 'rating-desc':
            return (candidates) => sortCandidatesByRating(candidates, 'desc');
        case 'rating-asc':
            return (candidates) => sortCandidatesByRating(candidates, 'asc');
        case 'name-asc':
            return (candidates) => sortCandidatesByName(candidates, 'asc');
        case 'name-desc':
            return (candidates) => sortCandidatesByName(candidates, 'desc');
        case 'filename-asc':
            return (candidates) => sortCandidatesByFilename(candidates, 'asc');
        case 'filename-desc':
            return (candidates) => sortCandidatesByFilename(candidates, 'desc');
        case 'percentage-desc':
            return (candidates) => sortCandidatesByPercentage(candidates, 'desc');
        case 'percentage-asc':
            return (candidates) => sortCandidatesByPercentage(candidates, 'asc');
        default:
            return (candidates) => sortCandidatesByRating(candidates, 'desc');
    }
};
