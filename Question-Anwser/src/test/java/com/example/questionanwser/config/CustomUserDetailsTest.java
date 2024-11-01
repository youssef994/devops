package com.example.questionanwser.config;

import com.example.questionanwser.Model.Role;
import com.example.questionanwser.Model.UserCredentials;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    private UserCredentials userCredentials;
    private CustomUserDetails customUserDetails;

    @BeforeEach
    void setUp() {
        // Setup test data
        userCredentials = new UserCredentials();
        userCredentials.setUsername("testUser");
        userCredentials.setPassword("testPassword");
        userCredentials.setRole(Role.User); // Assuming Role is an enum

        // Initialize CustomUserDetails with UserCredentials
        customUserDetails = new CustomUserDetails(userCredentials);
    }

    @Test
    void testGetUsername() {
        assertEquals("testUser", customUserDetails.getUsername());
    }

    @Test
    void testGetPassword() {
        assertEquals("testPassword", customUserDetails.getPassword());
    }

    @Test
    void testGetAuthorities() {
        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();
        assertEquals(1, authorities.size());
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_User")));
    }

    @Test
    void testIsAccountNonExpired() {
        assertTrue(customUserDetails.isAccountNonExpired());
    }

    @Test
    void testIsAccountNonLocked() {
        assertTrue(customUserDetails.isAccountNonLocked());
    }

    @Test
    void testIsCredentialsNonExpired() {
        assertTrue(customUserDetails.isCredentialsNonExpired());
    }

    @Test
    void testIsEnabled() {
        assertTrue(customUserDetails.isEnabled());
    }
}
