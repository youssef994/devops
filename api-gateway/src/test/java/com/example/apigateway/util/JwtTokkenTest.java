package com.example.apigateway.util;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class JwtTokkenTest {

    @Autowired
    private JwtTokken jwtTokken;

    @Test
    public void testValidToken() {
        String validToken = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6W10sImlkIjoxLCJzdWIiOiJ0ZXN0VXNlciIsImlhdCI6MTczMDM4NjQxNSwiZXhwIjoxNzMwMzg4MjE1fQ.yqn3nYr8UR3Vwc5knzH8rN1HjFAu_t0Y-Kz5B7tZ96I";
        jwtTokken.validateToken(validToken); // Should not throw an exception
    }

    @Test
    public void testInvalidToken() {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6W10sImlkIjoxLCJzdWIiOiJ0ZXN0VXNlciIsImlhdCI6MTczMDM4XhwIjoxNzMwMzg4MjE1fQ.yqn3nYr8UR3Vwc5knzH8rN1HjFAu_t0Y-Kz5B7tZ96I"; // An invalid token
        assertThrows(JwtException.class, () -> jwtTokken.validateToken(invalidToken));
    }

}
