/**
 * Utility functions for data formatting and display
 * Provides consistent formatting across the application
 */

/**
 * Format number with locale-specific separators
 * @param {number} num - Number to format
 * @returns {string} Formatted number string
 */
export const formatNumber = (num) => {
    if (num === null || num === undefined) return 'N/A';
    return new Intl.NumberFormat().format(num);
};

/**
 * Format currency amount
 * @param {number} amount - Amount to format
 * @param {string} currency - Currency code (default: USD)
 * @returns {string} Formatted currency string
 */
export const formatCurrency = (amount, currency = 'USD') => {
    if (amount === null || amount === undefined) return 'N/A';
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: currency,
        minimumFractionDigits: 4,
        maximumFractionDigits: 4
    }).format(amount);
};

/**
 * Format date to locale string
 * @param {Date|string} date - Date to format
 * @returns {string} Formatted date string
 */
export const formatDate = (date) => {
    if (!date) return 'N/A';
    return new Date(date).toLocaleString();
};

/**
 * Format time to locale time string
 * @param {Date|string} date - Date to format
 * @returns {string} Formatted time string
 */
export const formatTime = (date) => {
    if (!date) return 'N/A';
    return new Date(date).toLocaleTimeString();
};

/**
 * Get health status color scheme
 * @param {string} status - Health status
 * @returns {Object} Color scheme object
 */
export const getHealthStatusColor = (status) => {
    switch (status?.toUpperCase()) {
        case 'UP':
            return { status: 'UP', color: 'green' };
        case 'DOWN':
            return { status: 'DOWN', color: 'red' };
        default:
            return { status: status || 'unknown', color: 'gray' };
    }
};

/**
 * Get prompt type color scheme
 * @param {string} type - Prompt type
 * @returns {string} Color scheme name
 */
export const getPromptTypeColor = (type) => {
    return type === 'summary' ? 'blue' : 'green';
};

/**
 * Get prompt role color scheme
 * @param {string} role - Prompt role
 * @returns {string} Color scheme name
 */
export const getPromptRoleColor = (role) => {
    return role === 'system' ? 'purple' : 'orange';
};

/**
 * Copy text to clipboard
 * @param {string} text - Text to copy
 * @returns {Promise<boolean>} Success status
 */
export const copyToClipboard = async (text) => {
    try {
        await navigator.clipboard.writeText(text);
        return true;
    } catch (err) {
        console.error('Failed to copy text to clipboard:', err);
        return false;
    }
};

/**
 * Validate email format
 * @param {string} email - Email to validate
 * @returns {boolean} Validation result
 */
export const isValidEmail = (email) => {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
};

/**
 * Truncate text to specified length
 * @param {string} text - Text to truncate
 * @param {number} maxLength - Maximum length
 * @param {string} suffix - Suffix to add (default: '...')
 * @returns {string} Truncated text
 */
export const truncateText = (text, maxLength, suffix = '...') => {
    if (!text || text.length <= maxLength) return text;
    return text.substring(0, maxLength - suffix.length) + suffix;
};

/**
 * Capitalize first letter of string
 * @param {string} str - String to capitalize
 * @returns {string} Capitalized string
 */
export const capitalize = (str) => {
    if (!str) return '';
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
};

/**
 * Convert bytes to human readable format
 * @param {number} bytes - Bytes to convert
 * @param {number} decimals - Number of decimal places
 * @returns {string} Human readable size
 */
export const formatBytes = (bytes, decimals = 2) => {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const dm = decimals < 0 ? 0 : decimals;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
    
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(dm)) + ' ' + sizes[i];
};
