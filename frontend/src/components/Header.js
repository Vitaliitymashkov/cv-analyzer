import React from 'react';
import { Box, Image, Heading } from '@chakra-ui/react';
import logo from '../assets/logo.png';

function Header() {
    return (
        <Box display="flex" alignItems="center" gap={3} p={4}>
            <Image src={logo} alt="Logo" boxSize="40px" borderRadius="md" />
            <Heading as="h1" size="lg">CV Analyzer</Heading>
        </Box>
    );
}

export default Header;