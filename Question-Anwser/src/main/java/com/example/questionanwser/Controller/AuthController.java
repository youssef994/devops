package com.example.questionanwser.Controller;

import com.example.questionanwser.Model.Role;
import com.example.questionanwser.Model.UserCredentials;
import com.example.questionanwser.Service.AuthenticationService;
import dto.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authService;

    @Autowired
    AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<UserCredentials> registerUser(@RequestBody UserCredentials user) {
        try {
            UserCredentials registeredUser = authService.registerUser(user);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            authService.changePassword(request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok("Password changed successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }


    @GetMapping("/verify-email")
    public ResponseEntity<Object> verifyEmail(@RequestParam String code) {
        boolean isVerified = authService.verifyEmail(code);
        if (isVerified) {
            return ResponseEntity.ok().body("{\"message\": \"Email verified successfully!\"}");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"error\": \"Invalid verification code.\"}");
        }
    }

    @PostMapping("/token")
    public ResponseEntity<UserCredentialResponse> getToken(@RequestBody AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        if (authenticate.isAuthenticated()) {
            String token = authService.generateToken(authRequest.getUsername());
            Optional<UserCredentials> userOptional = authService.getUserByUsername(authRequest.getUsername());

            if (!userOptional.isPresent()) {
                throw new RuntimeException("User not found.");
            }

            UserCredentials user = userOptional.get();
            user.setLastLoginDate(LocalDateTime.now());
            user.setLoginCount(user.getLoginCount() + 1); // Increment login count
            authService.updateUser(user.getId(), user);

            UserCredentialResponse response = new UserCredentialResponse();
            response.setToken(token);
            response.setLoginCount(user.getLoginCount()); // Set login count in the response
            UserCredentials safeUser = new UserCredentials();
            BeanUtils.copyProperties(user, safeUser);
            safeUser.setPassword(null);
            response.setUser(safeUser);

            return ResponseEntity.ok(response);
        } else {
            throw new RuntimeException("Invalid access");
        }
    }

    @GetMapping("/byRole")
    public List<UserCredentials> getAllUsersByRole(@RequestParam Role role) {
        return authService.getAllUsersByRole(role);
    }

    @PostMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestBody TokenValidationRequest request) {
        try {
            authService.validate(request.getToken());
            return new ResponseEntity<>("Token is valid", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Token is invalid", HttpStatus.UNAUTHORIZED);
        }
    }



    @GetMapping("/getById/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer id) {
        Optional<UserCredentials> userOptional = authService.getUserById(id);

        if (userOptional.isPresent()) {
            UserCredentials user = userOptional.get();
            UserResponse userResponce = new UserResponse();
            userResponce.setId(user.getId());
            userResponce.setUsername(user.getUsername());
            userResponce.setEmail(user.getEmail());

            return new ResponseEntity<>(userResponce, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/currentUser")
    public ResponseEntity<UserResponse> getCurrentUser() {
        Optional<UserCredentials> userOptional = authService.getCurrentUser();

        if (userOptional.isPresent()) {
            UserCredentials user = userOptional.get();
            UserResponse userResponse = new UserResponse();
            BeanUtils.copyProperties(user, userResponse);

            return new ResponseEntity<>(userResponse, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/AllUsers")
    public List<UserCredentials> getAllUsers() {
        return authService.getAllUsers();
    }

    @GetMapping("/role")
    public List<UserCredentials> getUsersByRole(@RequestParam Role role) {
        return authService.getUsersByRole(role);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserCredentials> getUserById(@PathVariable int id) {
        Optional<UserCredentials> user = authService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserCredentials createUser(@RequestBody UserCredentials user) {
        return authService.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserCredentials> updateUser(@PathVariable int id, @RequestBody UserCredentials userDetails) {
        try {
            UserCredentials updatedUser = authService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        authService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/user/search")
    public List<UserCredentials> searchByUsername(@RequestParam("username") String username) {
        return authService.findByUsernameContainingIgnoreCase(username);
    }

    @GetMapping("/countTotalUsers")
    public long countTotalUsers() {
        return authService.countTotalUsers();
    }

    @GetMapping("/countActiveUsers")
    public long countActiveUsers() {
        return authService.countActiveUsers();
    }

    @GetMapping("/countNewUsersToday")
    public long countNewUsersToday() {
        return authService.countNewUsersToday();
    }

    @GetMapping("/countNewUsersThisMonth")
    public long countNewUsersThisMonth() {
        return authService.countNewUsersThisMonth();
    }
}