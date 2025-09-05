/**
 * HealthCard component for displaying individual health metrics
 * Provides consistent styling for health metric cards
 */

import React from 'react';
import { Box, Heading, Text, Stat, StatLabel, StatNumber, StatHelpText, StatArrow, Badge } from '@chakra-ui/react';
import { formatNumber, formatCurrency, formatDate, getHealthStatusColor } from '../../utils/formatters';

/**
 * HealthCard component
 * @param {Object} props - Component props
 * @param {string} props.title - Card title
 * @param {Object} props.data - Health data object
 * @param {string} props.type - Card type for rendering logic
 * @returns {JSX.Element} HealthCard component
 */
const HealthCard = ({ title, data, type }) => {
    const renderCardContent = () => {
        switch (type) {
            case 'system-health':
                const healthStatus = getHealthStatusColor(data?.status);
                return (
                    <Stat>
                        <StatLabel>Backend Status</StatLabel>
                        <StatNumber>
                            <Badge colorScheme={healthStatus.color} fontSize="md">
                                {healthStatus.status}
                            </Badge>
                        </StatNumber>
                        <StatHelpText>
                            {data?.status === 'UP' ? 'All systems operational' : 'System issues detected'}
                        </StatHelpText>
                    </Stat>
                );

            case 'genai-operations':
                return (
                    <Stat>
                        <StatLabel>Total Requests</StatLabel>
                        <StatNumber>
                            {formatNumber(data?.measurements?.[0]?.value)}
                        </StatNumber>
                        <StatHelpText>
                            <StatArrow type="increase" />
                            LLM API calls made
                        </StatHelpText>
                    </Stat>
                );

            case 'token-usage':
                return (
                    <Stat>
                        <StatLabel>Total Tokens</StatLabel>
                        <StatNumber>
                            {formatNumber(data?.measurements?.[0]?.value)}
                        </StatNumber>
                        <StatHelpText>
                            <StatArrow type="increase" />
                            Tokens consumed
                        </StatHelpText>
                    </Stat>
                );

            case 'api-costs':
                return (
                    <Stat>
                        <StatLabel>Total Cost</StatLabel>
                        <StatNumber>
                            {formatCurrency(data?.totalCost, data?.pricing?.currency)}
                        </StatNumber>
                        <StatHelpText>
                            <StatArrow type="increase" />
                            API usage cost
                        </StatHelpText>
                    </Stat>
                );

            case 'input-tokens':
                return (
                    <Stat>
                        <StatLabel>Total Input Tokens</StatLabel>
                        <StatNumber>
                            {formatNumber(data?.totalInputTokens)}
                        </StatNumber>
                        <StatHelpText>
                            <StatArrow type="increase" />
                            Prompt tokens
                        </StatHelpText>
                    </Stat>
                );

            case 'output-tokens':
                return (
                    <Stat>
                        <StatLabel>Total Output Tokens</StatLabel>
                        <StatNumber>
                            {formatNumber(data?.totalOutputTokens)}
                        </StatNumber>
                        <StatHelpText>
                            <StatArrow type="increase" />
                            Response tokens
                        </StatHelpText>
                    </Stat>
                );

            case 'operation-details':
                if (!data) return null;
                return (
                    <Box>
                        <Text fontSize="sm" color="gray.600" mb={2}>
                            <strong>Base Unit:</strong> {data.baseUnit || 'N/A'}
                        </Text>
                        <Text fontSize="sm" color="gray.600" mb={2}>
                            <strong>Description:</strong> {data.description || 'N/A'}
                        </Text>
                    </Box>
                );

            case 'token-details':
                if (!data) return null;
                return (
                    <Box>
                        <Text fontSize="sm" color="gray.600" mb={2}>
                            <strong>Base Unit:</strong> {data.baseUnit || 'N/A'}
                        </Text>
                        <Text fontSize="sm" color="gray.600" mb={2}>
                            <strong>Description:</strong> {data.description || 'N/A'}
                        </Text>
                    </Box>
                );

            case 'pricing-info':
                if (!data) return null;
                return (
                    <Box>
                        <Text fontSize="sm" color="gray.600" mb={2}>
                            <strong>Input Tokens:</strong> {formatCurrency(data.inputTokensPerMillion, data.currency)} per 1M tokens
                        </Text>
                        <Text fontSize="sm" color="gray.600" mb={2}>
                            <strong>Output Tokens:</strong> {formatCurrency(data.outputTokensPerMillion, data.currency)} per 1M tokens
                        </Text>
                        <Text fontSize="sm" color="gray.600" mb={2}>
                            <strong>Currency:</strong> {data.currency}
                        </Text>
                    </Box>
                );

            case 'latest-ai-call':
                if (!data) return null;
                return (
                    <Box>
                        <Text fontSize="sm" color="gray.600" mb={2}>
                            <strong>Timestamp:</strong> {formatDate(data.timestamp)}
                        </Text>
                        <Text fontSize="sm" color="gray.600" mb={2}>
                            <strong>Input Tokens:</strong> {formatNumber(data.inputTokens)}
                        </Text>
                        <Text fontSize="sm" color="gray.600" mb={2}>
                            <strong>Output Tokens:</strong> {formatNumber(data.outputTokens)}
                        </Text>
                        <Text fontSize="sm" color="gray.600" mb={2}>
                            <strong>Total Tokens:</strong> {formatNumber(data.inputTokens + data.outputTokens)}
                        </Text>
                        <Text fontSize="sm" color="gray.600" mb={2}>
                            <strong>Input Cost:</strong> {formatCurrency(data.inputCost, data.currency)}
                        </Text>
                        <Text fontSize="sm" color="gray.600" mb={2}>
                            <strong>Output Cost:</strong> {formatCurrency(data.outputCost, data.currency)}
                        </Text>
                        <Text fontSize="sm" color="gray.600" mb={2}>
                            <strong>Total Cost:</strong> {formatCurrency(data.totalCost, data.currency)}
                        </Text>
                    </Box>
                );

            case 'health-details':
                if (!data) return null;
                return (
                    <Box>
                        {Object.entries(data.components || {}).map(([key, value]) => (
                            <Box key={key} mb={2}>
                                <Text fontSize="sm">
                                    <strong>{key}:</strong> 
                                    <Badge 
                                        ml={2} 
                                        colorScheme={value.status === 'UP' ? 'green' : 'red'}
                                        size="sm"
                                    >
                                        {value.status}
                                    </Badge>
                                </Text>
                            </Box>
                        ))}
                    </Box>
                );

            default:
                return null;
        }
    };

    return (
        <Box>
            <Heading size="sm" mb={4}>{title}</Heading>
            {renderCardContent()}
        </Box>
    );
};

export default HealthCard;
