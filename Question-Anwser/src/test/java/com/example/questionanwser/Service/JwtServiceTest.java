package com.example.questionanwser.Service;

import com.example.questionanwser.Model.UserCredentials;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.*;

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
    void validateToken_Expired() throws InterruptedException {
        // Arrange
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getAuthorities()).thenReturn(Collections.emptyList());

        // Generate a token with a very short expiration (1 millisecond)
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", 1);  // Add a dummy user ID for testing

        String expiredToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1))  // Expire almost immediately
                .signWith(jwtService.getSignKey(), SignatureAlgorithm.HS256)
                .compact();

        // Wait for the token to expire
        Thread.sleep(10);

        // Act & Assert
        assertThrows(JwtException.class, () -> jwtService.validateToken(expiredToken));
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
