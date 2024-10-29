package com.example.questionanwser.Service;

import com.example.questionanwser.Model.UserCredentials;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jwtService = new JwtService();
    }

    @Test
    void generateToken() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        // Act
        String token = jwtService.generateToken(userDetails, 1);

        // Assert
        assertNotNull(token);
    }
    @Test
    void getUsernameFromToken() {
        // Arrange
        String username = "testUser";

        // Create a mock UserDetails and set the username
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);

        // Generate a token using the mocked UserDetails
        String token = jwtService.generateToken(userDetails, 1); // UserId is arbitrary here

        // Act
        String extractedUsername = jwtService.getUsernameFromToken(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    void validateToken_Expired() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        String expiredToken = jwtService.generateToken(userDetails, 1);
        // Simulate token expiration by manipulating its expiration time
        String manipulatedToken = expiredToken.replace("eyJhbGciOiJIUzI1NiJ9", "eyJhbGciOiJIUzI1NiJ9").replaceFirst("\\.\\w{2,}\\.\\w{2,}$", ".eyJleHBfaGAoWFVJaUJkZS5EYXRlIjp7InRpbWVPciI6MTYwMDEwMDAwMDAwMCwiYXJndW1lbnQiOiJ0ZXN0VXNlciIsInN1YiI6InRlc3RVc2VyIn19"); // Simulate an expired token

        // Act & Assert
        assertThrows(JwtException.class, () -> jwtService.validateToken(manipulatedToken));
    }

    @Test
    void validateToken_Valid() {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        String token = jwtService.generateToken(userDetails, 1);

        // Act
        boolean isValid = jwtService.validateToken(token, userDetails);

        // Assert
        assertTrue(isValid);
    }



}
