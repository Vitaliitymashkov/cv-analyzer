/**
 * Simple error handling utilities for the CV Analyzer frontend
 * Works with the simplified backend error response format
 */

/**
 * Simple error types based on HTTP status codes
 */
export const ERROR_TYPES = {
    RATE_LIMIT: 'RATE_LIMIT',
    VALIDATION: 'VALIDATION',
    INTERNAL_ERROR: 'INTERNAL_ERROR',
    NETWORK_ERROR: 'NETWORK_ERROR',
    AUTHENTICATION: 'AUTHENTICATION',
    AUTHORIZATION: 'AUTHORIZATION',
    NOT_FOUND: 'NOT_FOUND',
    CONFLICT: 'CONFLICT',
    UNSUPPORTED_MEDIA_TYPE: 'UNSUPPORTED_MEDIA_TYPE',
    SERVICE_UNAVAILABLE: 'SERVICE_UNAVAILABLE'
};

/**
 * User-friendly error messages for different error types
 */
export const ERROR_MESSAGES = {
    [ERROR_TYPES.RATE_LIMIT]: {
        title: 'Rate Limit Exceeded',
        message: 'AI service rate limit exceeded. Please try later.',
        action: 'Please try again in a few minutes.',
        severity: 'warning'
    },
    [ERROR_TYPES.VALIDATION]: {
        title: 'Invalid Input',
        message: 'Please check your input and try again.',
        action: 'Make sure all required fields are filled correctly.',
        severity: 'error'
    },
    [ERROR_TYPES.INTERNAL_ERROR]: {
        title: 'System Error',
        message: 'An unexpected error occurred.',
        action: 'Please try again later or contact support.',
        severity: 'error'
    },
    [ERROR_TYPES.NETWORK_ERROR]: {
        title: 'Network Error',
        message: 'Unable to connect to the server.',
        action: 'Please check your internet connection and try again.',
        severity: 'error'
    },
    [ERROR_TYPES.AUTHENTICATION]: {
        title: 'Authentication Required',
        message: 'Please log in to continue.',
        action: 'Please log in and try again.',
        severity: 'error'
    },
    [ERROR_TYPES.AUTHORIZATION]: {
        title: 'Access Denied',
        message: 'You do not have permission to perform this action.',
        action: 'Please contact your administrator.',
        severity: 'error'
    },
    [ERROR_TYPES.NOT_FOUND]: {
        title: 'Not Found',
        message: 'The requested resource was not found.',
        action: 'Please check your request and try again.',
        severity: 'error'
    },
    [ERROR_TYPES.CONFLICT]: {
        title: 'Conflict',
        message: 'The request conflicts with existing data.',
        action: 'Please check your data and try again.',
        severity: 'error'
    },
    [ERROR_TYPES.UNSUPPORTED_MEDIA_TYPE]: {
        title: 'Unsupported File Type',
        message: 'The file type is not supported.',
        action: 'Please use a supported file format.',
        severity: 'error'
    },
    [ERROR_TYPES.SERVICE_UNAVAILABLE]: {
        title: 'Service Unavailable',
        message: 'The service is temporarily unavailable.',
        action: 'Please try again later.',
        severity: 'warning'
    }
};

/**
 * Parse error response from backend
 * @param {Object} error - Axios error object
 * @returns {Object} Parsed error information
 */
export const parseError = (error) => {
    console.error('Error occurred:', error);

    // Network error (no response received)
    if (!error.response) {
        return {
            type: ERROR_TYPES.NETWORK_ERROR,
            title: 'Network Error',
            message: 'Unable to connect to the server. Please check your internet connection.',
            action: 'Please check your connection and try again.',
            severity: 'error'
        };
    }

    const { status, data } = error.response;
    const errorData = data || {};

    // Parse simplified backend error response
    if (errorData.status && errorData.error && errorData.message) {
        const errorType = getErrorTypeFromStatus(errorData.status);
        const errorInfo = ERROR_MESSAGES[errorType] || ERROR_MESSAGES[ERROR_TYPES.INTERNAL_ERROR];
        
        return {
            type: errorType,
            title: errorInfo.title,
            message: errorData.message, // Use backend message directly
            action: errorInfo.action,
            severity: errorInfo.severity
        };
    }

    // Fallback to HTTP status code
    return parseHttpStatusError(status, errorData.message || error.message);
};

/**
 * Get error type from HTTP status code
 * @param {number} status - HTTP status code
 * @returns {string} Error type
 */
const getErrorTypeFromStatus = (status) => {
    const statusErrorMap = {
        400: ERROR_TYPES.VALIDATION,
        401: ERROR_TYPES.AUTHENTICATION,
        403: ERROR_TYPES.AUTHORIZATION,
        404: ERROR_TYPES.NOT_FOUND,
        409: ERROR_TYPES.CONFLICT,
        415: ERROR_TYPES.UNSUPPORTED_MEDIA_TYPE,
        429: ERROR_TYPES.RATE_LIMIT,
        500: ERROR_TYPES.INTERNAL_ERROR,
        502: ERROR_TYPES.SERVICE_UNAVAILABLE,
        503: ERROR_TYPES.SERVICE_UNAVAILABLE,
        504: ERROR_TYPES.SERVICE_UNAVAILABLE
    };
    return statusErrorMap[status] || ERROR_TYPES.INTERNAL_ERROR;
};

/**
 * Parse HTTP status code to error information
 * @param {number} status - HTTP status code
 * @param {string} message - Error message
 * @returns {Object} Error information
 */
const parseHttpStatusError = (status, message) => {
    const errorType = getErrorTypeFromStatus(status);
    const errorInfo = ERROR_MESSAGES[errorType] || ERROR_MESSAGES[ERROR_TYPES.INTERNAL_ERROR];
    
    return {
        type: errorType,
        title: errorInfo.title,
        message: message || errorInfo.message,
        action: errorInfo.action,
        severity: errorInfo.severity
    };
};

/**
 * Get user-friendly error message for display
 * @param {Object} errorInfo - Parsed error information
 * @returns {string} User-friendly error message
 */
export const getUserFriendlyMessage = (errorInfo) => {
    return errorInfo.message;
};
