import React, { useState, useEffect } from 'react';
import { 
    Box, 
    Heading, 
    Text, 
    Stat, 
    StatLabel, 
    StatNumber, 
    StatHelpText, 
    StatArrow,
    SimpleGrid,
    Card,
    CardBody,
    CardHeader,
    Badge,
    Spinner,
    Alert,
    AlertIcon,
    Button,
    useColorModeValue
} from '@chakra-ui/react';
import { InfoIcon, RepeatIcon } from '@chakra-ui/icons';
import axios from 'axios';

function HealthPage() {
    const [metrics, setMetrics] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [lastUpdated, setLastUpdated] = useState(null);

    const cardBg = useColorModeValue('white', 'gray.800');
    const cardBorder = useColorModeValue('gray.200', 'gray.700');

    const fetchMetrics = async () => {
        try {
            setLoading(true);
            setError('');
            
            // Fetch multiple metrics in parallel
            const [healthResponse, operationResponse, tokenResponse, costResponse] = await Promise.allSettled([
                axios.get('/actuator/health'),
                axios.get('/actuator/metrics/gen_ai.client.operation'),
                axios.get('/actuator/metrics/gen_ai.client.token.usage'),
                axios.get('/api/cost/metrics')
            ]);

            const metricsData = {
                health: healthResponse.status === 'fulfilled' ? healthResponse.value.data : null,
                operations: operationResponse.status === 'fulfilled' ? operationResponse.value.data : null,
                tokens: tokenResponse.status === 'fulfilled' ? tokenResponse.value.data : null,
                cost: costResponse.status === 'fulfilled' ? costResponse.value.data : null
            };

            setMetrics(metricsData);
            setLastUpdated(new Date());
        } catch (err) {
            console.error('Error fetching metrics:', err);
            setError('Failed to fetch health metrics. Make sure the backend is running.');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchMetrics();
        // Auto-refresh every 30 seconds
        const interval = setInterval(fetchMetrics, 30000);
        return () => clearInterval(interval);
    }, []);

    const formatNumber = (num) => {
        if (num === null || num === undefined) return 'N/A';
        return new Intl.NumberFormat().format(num);
    };

    const formatCurrency = (amount, currency = 'USD') => {
        if (amount === null || amount === undefined) return 'N/A';
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: currency,
            minimumFractionDigits: 4,
            maximumFractionDigits: 4
        }).format(amount);
    };

    const getHealthStatus = () => {
        if (!metrics?.health) return { status: 'unknown', color: 'gray' };
        const status = metrics.health.status;
        return {
            status: status,
            color: status === 'UP' ? 'green' : status === 'DOWN' ? 'red' : 'yellow'
        };
    };

    const healthStatus = getHealthStatus();

    return (
        <Box as="section" mb={8}>
            <Box display="flex" alignItems="center" justifyContent="space-between" flexWrap="wrap" gap={4} mb={6}>
                <Heading as="h2" size="lg" display="flex" alignItems="center" gap={2}>
                    <InfoIcon />
                    System Health & GenAI Metrics
                </Heading>
                <Button 
                    onClick={fetchMetrics} 
                    isLoading={loading}
                    leftIcon={<RepeatIcon />}
                    colorScheme="blue"
                    size="sm"
                >
                    Refresh
                </Button>
            </Box>

            {error && (
                <Alert status="error" mb={4}>
                    <AlertIcon />
                    {error}
                </Alert>
            )}

            {loading && !metrics && (
                <Box display="flex" alignItems="center" gap={3} mb={4}>
                    <Spinner size="sm" />
                    <Text>Loading metrics...</Text>
                </Box>
            )}

            {lastUpdated && (
                <Text fontSize="sm" color="gray.500" mb={4}>
                    Last updated: {lastUpdated.toLocaleTimeString()}
                </Text>
            )}

            <SimpleGrid columns={{ base: 1, md: 2, lg: 3 }} spacing={6}>
                {/* System Health */}
                <Card bg={cardBg} borderColor={cardBorder}>
                    <CardHeader>
                        <Heading size="sm">System Health</Heading>
                    </CardHeader>
                    <CardBody>
                        <Stat>
                            <StatLabel>Backend Status</StatLabel>
                            <StatNumber>
                                <Badge colorScheme={healthStatus.color} fontSize="md">
                                    {healthStatus.status}
                                </Badge>
                            </StatNumber>
                            <StatHelpText>
                                {metrics?.health?.status === 'UP' ? 'All systems operational' : 'System issues detected'}
                            </StatHelpText>
                        </Stat>
                    </CardBody>
                </Card>

                {/* GenAI Operations */}
                <Card bg={cardBg} borderColor={cardBorder}>
                    <CardHeader>
                        <Heading size="sm">GenAI Operations</Heading>
                    </CardHeader>
                    <CardBody>
                        <Stat>
                            <StatLabel>Total Requests</StatLabel>
                            <StatNumber>
                                {formatNumber(metrics?.operations?.measurements?.[0]?.value)}
                            </StatNumber>
                            <StatHelpText>
                                <StatArrow type="increase" />
                                LLM API calls made
                            </StatHelpText>
                        </Stat>
                    </CardBody>
                </Card>

                {/* Token Usage */}
                <Card bg={cardBg} borderColor={cardBorder}>
                    <CardHeader>
                        <Heading size="sm">Token Usage</Heading>
                    </CardHeader>
                    <CardBody>
                        <Stat>
                            <StatLabel>Total Tokens</StatLabel>
                            <StatNumber>
                                {formatNumber(metrics?.tokens?.measurements?.[0]?.value)}
                            </StatNumber>
                            <StatHelpText>
                                <StatArrow type="increase" />
                                Tokens consumed
                            </StatHelpText>
                        </Stat>
                    </CardBody>
                </Card>

                {/* Cost Tracking */}
                <Card bg={cardBg} borderColor={cardBorder}>
                    <CardHeader>
                        <Heading size="sm">API Costs</Heading>
                    </CardHeader>
                    <CardBody>
                        <Stat>
                            <StatLabel>Total Cost</StatLabel>
                            <StatNumber>
                                {formatCurrency(metrics?.cost?.totalCost, metrics?.cost?.pricing?.currency)}
                            </StatNumber>
                            <StatHelpText>
                                <StatArrow type="increase" />
                                OpenAI API usage
                            </StatHelpText>
                        </Stat>
                    </CardBody>
                </Card>

                {/* Input Tokens */}
                <Card bg={cardBg} borderColor={cardBorder}>
                    <CardHeader>
                        <Heading size="sm">Input Tokens</Heading>
                    </CardHeader>
                    <CardBody>
                        <Stat>
                            <StatLabel>Total Input Tokens</StatLabel>
                            <StatNumber>
                                {formatNumber(metrics?.cost?.totalInputTokens)}
                            </StatNumber>
                            <StatHelpText>
                                <StatArrow type="increase" />
                                Prompt tokens
                            </StatHelpText>
                        </Stat>
                    </CardBody>
                </Card>

                {/* Output Tokens */}
                <Card bg={cardBg} borderColor={cardBorder}>
                    <CardHeader>
                        <Heading size="sm">Output Tokens</Heading>
                    </CardHeader>
                    <CardBody>
                        <Stat>
                            <StatLabel>Total Output Tokens</StatLabel>
                            <StatNumber>
                                {formatNumber(metrics?.cost?.totalOutputTokens)}
                            </StatNumber>
                            <StatHelpText>
                                <StatArrow type="increase" />
                                Response tokens
                            </StatHelpText>
                        </Stat>
                    </CardBody>
                </Card>

                {/* Additional Metrics */}
                {metrics?.operations && (
                    <Card bg={cardBg} borderColor={cardBorder}>
                        <CardHeader>
                            <Heading size="sm">Operation Details</Heading>
                        </CardHeader>
                        <CardBody>
                            <Box>
                                <Text fontSize="sm" color="gray.600" mb={2}>
                                    <strong>Base Unit:</strong> {metrics.operations.baseUnit || 'N/A'}
                                </Text>
                                <Text fontSize="sm" color="gray.600" mb={2}>
                                    <strong>Description:</strong> {metrics.operations.description || 'N/A'}
                                </Text>
                            </Box>
                        </CardBody>
                    </Card>
                )}

                {metrics?.tokens && (
                    <Card bg={cardBg} borderColor={cardBorder}>
                        <CardHeader>
                            <Heading size="sm">Token Details</Heading>
                        </CardHeader>
                        <CardBody>
                            <Box>
                                <Text fontSize="sm" color="gray.600" mb={2}>
                                    <strong>Base Unit:</strong> {metrics.tokens.baseUnit || 'N/A'}
                                </Text>
                                <Text fontSize="sm" color="gray.600" mb={2}>
                                    <strong>Description:</strong> {metrics.tokens.description || 'N/A'}
                                </Text>
                            </Box>
                        </CardBody>
                    </Card>
                )}

                {/* Pricing Information */}
                {metrics?.cost?.pricing && (
                    <Card bg={cardBg} borderColor={cardBorder}>
                        <CardHeader>
                            <Heading size="sm">Pricing Information</Heading>
                        </CardHeader>
                        <CardBody>
                            <Box>
                                <Text fontSize="sm" color="gray.600" mb={2}>
                                    <strong>Input Tokens:</strong> {formatCurrency(metrics.cost.pricing.inputTokensPerMillion, metrics.cost.pricing.currency)} per 1M tokens
                                </Text>
                                <Text fontSize="sm" color="gray.600" mb={2}>
                                    <strong>Output Tokens:</strong> {formatCurrency(metrics.cost.pricing.outputTokensPerMillion, metrics.cost.pricing.currency)} per 1M tokens
                                </Text>
                                <Text fontSize="sm" color="gray.600" mb={2}>
                                    <strong>Currency:</strong> {metrics.cost.pricing.currency}
                                </Text>
                            </Box>
                        </CardBody>
                    </Card>
                )}

                {/* Latest AI Call */}
                {metrics?.cost?.latestAiCall && (
                    <Card bg={cardBg} borderColor={cardBorder}>
                        <CardHeader>
                            <Heading size="sm">Latest AI Call</Heading>
                        </CardHeader>
                        <CardBody>
                            <Box>
                                <Text fontSize="sm" color="gray.600" mb={2}>
                                    <strong>Timestamp:</strong> {new Date(metrics.cost.latestAiCall.timestamp).toLocaleString()}
                                </Text>
                                <Text fontSize="sm" color="gray.600" mb={2}>
                                    <strong>Input Tokens:</strong> {formatNumber(metrics.cost.latestAiCall.inputTokens)}
                                </Text>
                                <Text fontSize="sm" color="gray.600" mb={2}>
                                    <strong>Output Tokens:</strong> {formatNumber(metrics.cost.latestAiCall.outputTokens)}
                                </Text>
                                <Text fontSize="sm" color="gray.600" mb={2}>
                                    <strong>Total Tokens:</strong> {formatNumber(metrics.cost.latestAiCall.inputTokens + metrics.cost.latestAiCall.outputTokens)}
                                </Text>
                                <Text fontSize="sm" color="gray.600" mb={2}>
                                    <strong>Input Cost:</strong> {formatCurrency(metrics.cost.latestAiCall.inputCost, metrics.cost.pricing?.currency)}
                                </Text>
                                <Text fontSize="sm" color="gray.600" mb={2}>
                                    <strong>Output Cost:</strong> {formatCurrency(metrics.cost.latestAiCall.outputCost, metrics.cost.pricing?.currency)}
                                </Text>
                                <Text fontSize="sm" color="gray.600" mb={2}>
                                    <strong>Total Cost:</strong> {formatCurrency(metrics.cost.latestAiCall.totalCost, metrics.cost.pricing?.currency)}
                                </Text>
                            </Box>
                        </CardBody>
                    </Card>
                )}

                {/* Health Details */}
                {metrics?.health && (
                    <Card bg={cardBg} borderColor={cardBorder}>
                        <CardHeader>
                            <Heading size="sm">Health Details</Heading>
                        </CardHeader>
                        <CardBody>
                            <Box>
                                {Object.entries(metrics.health.components || {}).map(([key, value]) => (
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
                        </CardBody>
                    </Card>
                )}
            </SimpleGrid>
        </Box>
    );
}

export default HealthPage;
