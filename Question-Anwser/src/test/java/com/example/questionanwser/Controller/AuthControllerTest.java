package com.example.questionanwser.Controller;

import com.example.questionanwser.Model.Role;
import com.example.questionanwser.Model.UserCredentials;
import com.example.questionanwser.Service.AuthenticationService;
import dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationService authService;

    @Mock
    private AuthenticationManager authenticationManager;

    private UserCredentials userCredentials;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userCredentials = new UserCredentials();
        userCredentials.setId(1);
        userCredentials.setUsername("testUser");
        userCredentials.setEmail("test@example.com");
        userCredentials.setPassword("password");
        userCredentials.setLoginCount(0);
        userCredentials.setLastLoginDate(LocalDateTime.now());
    }

    @Test
    public void testRegisterUser() {
        when(authService.registerUser(any(UserCredentials.class))).thenReturn(userCredentials);

        ResponseEntity<UserCredentials> response = authController.registerUser(userCredentials);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(userCredentials, response.getBody());
    }

    @Test
    public void testRegisterUserConflict() {
        when(authService.registerUser(any(UserCredentials.class))).thenThrow(new RuntimeException("User already exists"));

        ResponseEntity<UserCredentials> response = authController.registerUser(userCredentials);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testChangePasswordSuccess() {
        // Arrange
        ChangePasswordRequest request = new ChangePasswordRequest("oldPassword", "newPassword");

        // Mock the changePassword method to return true
        when(authService.changePassword(anyString(), anyString())).thenReturn(true);

        // Act
        ResponseEntity<String> response = authController.changePassword(request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password changed successfully", response.getBody());
    }

    @Test
    public void testChangePasswordFailure() {
        ChangePasswordRequest request = new ChangePasswordRequest("oldPassword", "newPassword");
        doThrow(new RuntimeException("Invalid old password")).when(authService).changePassword(request.getOldPassword(), request.getNewPassword());

        ResponseEntity<String> response = authController.changePassword(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid old password", response.getBody());
    }

    @Test
    public void testVerifyEmailSuccess() {
        String verificationCode = "validCode";
        when(authService.verifyEmail(verificationCode)).thenReturn(true);

        ResponseEntity<Object> response = authController.verifyEmail(verificationCode);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"message\": \"Email verified successfully!\"}", response.getBody());
    }

    @Test
    public void testVerifyEmailFailure() {
        String verificationCode = "invalidCode";
        when(authService.verifyEmail(verificationCode)).thenReturn(false);

        ResponseEntity<Object> response = authController.verifyEmail(verificationCode);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("{\"error\": \"Invalid verification code.\"}", response.getBody());
    }



    @Test
    public void testGetTokenInvalidUser() {
        AuthRequest authRequest = new AuthRequest("testUser", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Invalid access"));

        assertThrows(RuntimeException.class, () -> authController.getToken(authRequest));
    }

    @Test
    public void testGetAllUsersByRole() {
        when(authService.getAllUsersByRole(Role.User)).thenReturn(Arrays.asList(userCredentials));

        List<UserCredentials> users = authController.getAllUsersByRole(Role.User);

        assertEquals(1, users.size());
        assertEquals(userCredentials, users.get(0));
    }

    @Test
    public void testValidateTokenSuccess() {
        TokenValidationRequest request = new TokenValidationRequest("validToken");
        doNothing().when(authService).validate(request.getToken());

        ResponseEntity<String> response = authController.validateToken(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Token is valid", response.getBody());
    }

    @Test
    public void testValidateTokenFailure() {
        TokenValidationRequest request = new TokenValidationRequest("invalidToken");
        doThrow(new RuntimeException("Token is invalid")).when(authService).validate(request.getToken());

        ResponseEntity<String> response = authController.validateToken(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Token is invalid", response.getBody());
    }

    @Test
    public void testGetUserByIdFound() {
        when(authService.getUserById(1)).thenReturn(Optional.of(userCredentials));

        ResponseEntity<UserCredentials> response = authController.getUserById(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userCredentials.getId(), response.getBody().getId());
        assertEquals(userCredentials.getUsername(), response.getBody().getUsername());
    }

    @Test
    public void testGetUserByIdNotFound() {
        when(authService.getUserById(1)).thenReturn(Optional.empty());

        ResponseEntity<UserCredentials> response = authController.getUserById(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetCurrentUserFound() {
        when(authService.getCurrentUser()).thenReturn(Optional.of(userCredentials));

        ResponseEntity<UserResponse> response = authController.getCurrentUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testGetCurrentUserNotFound() {
        when(authService.getCurrentUser()).thenReturn(Optional.empty());

        ResponseEntity<UserResponse> response = authController.getCurrentUser();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetAllUsers() {
        when(authService.getAllUsers()).thenReturn(Arrays.asList(userCredentials));

        List<UserCredentials> users = authController.getAllUsers();

        assertEquals(1, users.size());
        assertEquals(userCredentials, users.get(0));
    }

    @Test
    public void testGetUsersByRole() {
        when(authService.getUsersByRole(Role.User)).thenReturn(Arrays.asList(userCredentials));

        List<UserCredentials> users = authController.getUsersByRole(Role.User);

        assertEquals(1, users.size());
        assertEquals(userCredentials, users.get(0));
    }

    @Test
    public void testUpdateUserSuccess() {
        when(authService.updateUser(eq(1), any(UserCredentials.class))).thenReturn(userCredentials);

        ResponseEntity<UserCredentials> response = authController.updateUser(1, userCredentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userCredentials, response.getBody());
    }

    @Test
    public void testUpdateUserNotFound() {
        when(authService.updateUser(eq(1), any(UserCredentials.class))).thenThrow(new RuntimeException("User not found"));

        ResponseEntity<UserCredentials> response = authController.updateUser(1, userCredentials);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeleteUser() {
        doNothing().when(authService).deleteUser(1);

        ResponseEntity<Void> response = authController.deleteUser(1);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testSearchByUsername() {
        when(authService.findByUsernameContainingIgnoreCase("test")).thenReturn(Arrays.asList(userCredentials));

        List<UserCredentials> users = authController.searchByUsername("test");

        assertEquals(1, users.size());
        assertEquals(userCredentials, users.get(0));
    }

    @Test
    public void testCountTotalUsers() {
        when(authService.countTotalUsers()).thenReturn(100L);

        long totalUsers = authController.countTotalUsers();

        assertEquals(100L, totalUsers);
    }

    @Test
    public void testCountActiveUsers() {
        when(authService.countActiveUsers()).thenReturn(50L);

        long activeUsers = authController.countActiveUsers();

        assertEquals(50L, activeUsers);
    }



}
