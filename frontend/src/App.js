import React, { useState } from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css';
import Navigation from './components/Navigation';
import MatcherPage from './pages/MatcherPage';
import AdminPage from './pages/AdminPage';
import HealthPage from './pages/HealthPage';
import { Box, Flex, Heading, IconButton, useColorMode, useColorModeValue } from '@chakra-ui/react';
import { SunIcon, MoonIcon, HamburgerIcon } from '@chakra-ui/icons';

function App() {
    const [sidebarOpen, setSidebarOpen] = useState(false);
    const { colorMode, toggleColorMode } = useColorMode();
    const bg = useColorModeValue('gray.50', 'gray.900');
    const overlayBg = useColorModeValue('blackAlpha.500', 'blackAlpha.700');

    return (
        <BrowserRouter>
            <Box 
                className={`App ${!sidebarOpen ? 'sidebar-hidden' : ''}`} 
                bg={bg} 
                minH="100vh"
            >
                <Navigation isOpen={sidebarOpen} onNavigate={() => setSidebarOpen(false)} />
                {sidebarOpen && (
                    <Box
                        position="fixed"
                        inset={0}
                        bg={overlayBg}
                        zIndex={900}
                        onClick={() => setSidebarOpen(false)}
                    />
                )}
                <Box as="header" p={4} borderBottom="1px" borderColor={useColorModeValue('gray.200', 'gray.700')}>
                    <Flex align="center" justify="space-between" wrap="wrap" gap={4}>
                        <IconButton 
                            onClick={() => setSidebarOpen((v) => !v)} 
                            aria-label="Toggle menu" 
                            icon={<HamburgerIcon />} 
                            variant="ghost" 
                            colorScheme="gray" 
                        />
                        <Heading as="h1" size="lg">CV Analyzer</Heading>
                        <IconButton 
                            onClick={toggleColorMode} 
                            aria-label={`Switch to ${colorMode === 'light' ? 'dark' : 'light'} theme`} 
                            icon={colorMode === 'light' ? <MoonIcon /> : <SunIcon />} 
                            variant="ghost" 
                            colorScheme="yellow" 
                        />
                    </Flex>
                </Box>
                <Box as="main" p={6}>
                    <Routes>
                        <Route path="/" element={<MatcherPage />} />
                        <Route path="/admin" element={<AdminPage />} />
                        <Route path="/health" element={<HealthPage />} />
                    </Routes>
                </Box>
            </Box>
        </BrowserRouter>
    );
}

export default App;