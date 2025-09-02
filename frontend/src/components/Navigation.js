import React from 'react';
import { NavLink } from 'react-router-dom';
import { Box, VStack, Text, useColorModeValue } from '@chakra-ui/react';
import { SearchIcon, SettingsIcon, InfoIcon } from '@chakra-ui/icons';

function Navigation({ isOpen, onNavigate }) {
    const bg = useColorModeValue('white', 'gray.800');
    const borderColor = useColorModeValue('gray.200', 'gray.700');
    const textColor = useColorModeValue('gray.600', 'gray.300');
    const activeBg = useColorModeValue('purple.50', 'purple.900');
    const activeColor = useColorModeValue('purple.600', 'purple.200');

    return (
        <Box
            position="fixed"
            top={0}
            left={0}
            height="100vh"
            width="260px"
            transform={isOpen ? 'translateX(0)' : 'translateX(-100%)'}
            transition="transform 0.25s ease"
            bg={bg}
            borderRight="1px"
            borderColor={borderColor}
            boxShadow="lg"
            zIndex={1000}
            display={{ base: 'block', lg: isOpen ? 'block' : 'none' }}
        >
            <VStack spacing={4} p={6} align="stretch" height="100%">
                <Text fontSize="sm" fontWeight="bold" textTransform="uppercase" letterSpacing="wide" color={textColor}>
                    Menu
                </Text>
                <VStack spacing={2} align="stretch">
                    <Box
                        as={NavLink}
                        to="/"
                        end
                        onClick={onNavigate}
                        p={3}
                        borderRadius="md"
                        _hover={{ bg: activeBg }}
                        _activeLink={{ bg: activeBg, color: activeColor }}
                        display="flex"
                        alignItems="center"
                        gap={3}
                        color={textColor}
                        textDecoration="none"
                        transition="all 0.2s"
                    >
                        <SearchIcon />
                        <Text>Candidate Search</Text>
                    </Box>
                    <Box
                        as={NavLink}
                        to="/admin"
                        onClick={onNavigate}
                        p={3}
                        borderRadius="md"
                        _hover={{ bg: activeBg }}
                        _activeLink={{ bg: activeBg, color: activeColor }}
                        display="flex"
                        alignItems="center"
                        gap={3}
                        color={textColor}
                        textDecoration="none"
                        transition="all 0.2s"
                    >
                        <SettingsIcon />
                        <Text>Admin</Text>
                    </Box>
                    <Box
                        as={NavLink}
                        to="/health"
                        onClick={onNavigate}
                        p={3}
                        borderRadius="md"
                        _hover={{ bg: activeBg }}
                        _activeLink={{ bg: activeBg, color: activeColor }}
                        display="flex"
                        alignItems="center"
                        gap={3}
                        color={textColor}
                        textDecoration="none"
                        transition="all 0.2s"
                    >
                        <InfoIcon />
                        <Text>Health & Metrics</Text>
                    </Box>
                </VStack>
            </VStack>
        </Box>
    );
}

export default Navigation;