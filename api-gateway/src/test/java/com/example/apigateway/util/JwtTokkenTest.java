package com.example.apigateway.util;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class JwtTokkenTest {

    @Autowired
    private JwtTokken jwtTokken;

    private static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    private String generateValidToken(Long userId, String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", new String[]{}) // Include roles if needed
                .setId(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour expiry
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)), SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    public void testValidToken() {
        String validToken = generateValidToken(1L, "testUser"); // Generate a valid token
        jwtTokken.validateToken(validToken); // Should not throw an exception
    }

    @Test
    public void testInvalidToken() {
        String invalidToken = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6W10sImlkIjoxLCJzdWIiOiJ0ZXN0VXNlciIsImlhdCI6MTczMDM4XhwIjoxNzMwMzg4MjE1fQ.yqn3nYr8UR3Vwc5knzH8rN1HjFAu_t0Y-Kz5B7tZ96I"; // An invalid token
        assertThrows(JwtException.class, () -> jwtTokken.validateToken(invalidToken));
    }
}
