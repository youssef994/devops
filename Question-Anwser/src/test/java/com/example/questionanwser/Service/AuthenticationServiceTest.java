package com.example.questionanwser.Service;

import com.example.questionanwser.Model.UserCredentials;
import com.example.questionanwser.Repository.UserCredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;


    @Mock
    private UserCredentialRepository userCredentialRepository;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock Authentication
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn("youssef"); // Return username as a String

        // Mock SecurityContext
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // Set SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);
    }




    @Test
    void registerUser() {
        // Arrange
        UserCredentials user = new UserCredentials();
        user.setUsername("user1");
        user.setEmail("user1@example.com");
        user.setPassword("password");

        when(userCredentialRepository.findByUsername("user1")).thenReturn(Optional.empty());
        when(userCredentialRepository.findByEmail("user1@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userCredentialRepository.save(any(UserCredentials.class))).thenReturn(user);

        // Act
        UserCredentials registeredUser = authenticationService.registerUser(user);

        // Assert
        assertNotNull(registeredUser);
        assertEquals("user1", registeredUser.getUsername());

        // Verify the send method is called with any SimpleMailMessage
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void verifyEmail() {
        // Arrange
        String code = "123456";
        UserCredentials user = new UserCredentials();
        user.setVerificationCode(code);
        user.setIsVerified(false);

        when(userCredentialRepository.findByVerificationCode(code)).thenReturn(Optional.of(user));

        // Act
        boolean result = authenticationService.verifyEmail(code);

        // Assert
        assertTrue(result);
        assertTrue(user.isVerified());
    }
    @Test
    void registerUser_UsernameTaken() {
        // Arrange
        UserCredentials user = new UserCredentials();
        user.setUsername("existingUser");
        user.setEmail("user@example.com");
        user.setPassword("password");

        when(userCredentialRepository.findByUsername("existingUser")).thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.registerUser(user);
        });
        assertEquals("User registration failed", exception.getMessage());
    }
    @Test
    void registerUser_EmailTaken() {
        // Arrange
        UserCredentials user = new UserCredentials();
        user.setUsername("newUser");
        user.setEmail("existingUser@example.com");
        user.setPassword("password");

        when(userCredentialRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userCredentialRepository.findByEmail("existingUser@example.com")).thenReturn(Optional.of(user));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.registerUser(user);
        });
        assertEquals("User registration failed", exception.getMessage());
    }
    @Test
    void changePassword_Success() {
        // Arrange
        UserCredentials user = new UserCredentials();
        user.setUsername("youssef"); // Ensure this matches the username in the setUp
        user.setPassword(passwordEncoder.encode("oldPassword")); // Set the encoded password

        // Mock the user retrieval from the repository
        when(userCredentialRepository.findByUsername("youssef")).thenReturn(Optional.of(user));

        // Ensure the password encoder matches and encodes properly
        when(passwordEncoder.matches("oldPassword", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // Act
        boolean result = authenticationService.changePassword("oldPassword", "newPassword");

        // Assert
        assertTrue(result);
        verify(userCredentialRepository).save(user);
        assertEquals("encodedNewPassword", user.getPassword());
    }
    @Test
    void changePassword_OldPasswordIncorrect() {
        // Arrange
        UserCredentials user = new UserCredentials();
        user.setUsername("youssef");
        user.setPassword(passwordEncoder.encode("oldPassword")); // Set the encoded password

        when(userCredentialRepository.findByUsername("youssef")).thenReturn(Optional.of(user));

        // Ensure the password encoder matches incorrectly
        when(passwordEncoder.matches("wrongOldPassword", user.getPassword())).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.changePassword("wrongOldPassword", "newPassword");
        });
        assertEquals("Old password is incorrect", exception.getMessage());
    }
    @Test
    void getAllUsers() {
        // Arrange
        UserCredentials user1 = new UserCredentials();
        user1.setUsername("user1");

        UserCredentials user2 = new UserCredentials();
        user2.setUsername("user2");

        List<UserCredentials> users = Arrays.asList(user1, user2);
        when(userCredentialRepository.findAll()).thenReturn(users);

        // Act
        List<UserCredentials> allUsers = authenticationService.getAllUsers();

        // Assert
        assertNotNull(allUsers);
        assertEquals(2, allUsers.size());
        assertEquals("user1", allUsers.get(0).getUsername());
        assertEquals("user2", allUsers.get(1).getUsername());
    }



}
