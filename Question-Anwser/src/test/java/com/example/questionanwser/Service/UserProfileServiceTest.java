package com.example.questionanwser.Service;

import com.example.questionanwser.Model.UserCredentials;
import com.example.questionanwser.Model.UserProfile;
import com.example.questionanwser.Repository.AnswerRepository;
import com.example.questionanwser.Repository.PostRepository;
import com.example.questionanwser.Repository.UserCredentialRepository;
import com.example.questionanwser.Repository.UserProfileRepository;
import dto.UpdateUserProfileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {

    @InjectMocks
    private UserProfileService userProfileService;

    @Mock
    private UserCredentialRepository userCredentialsRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private AnswerRepository answerRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateUserProfile() {
        // Arrange
        int userId = 1;
        UpdateUserProfileDTO dto = new UpdateUserProfileDTO();
        dto.setProfilePictureUrl("http://example.com/pic.jpg");

        UserCredentials user = new UserCredentials();
        user.setId(userId);
        UserProfile userProfile = new UserProfile();
        user.setUserProfile(userProfile);

        when(userCredentialsRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(userProfile);
        when(userCredentialsRepository.save(any(UserCredentials.class))).thenReturn(user);

        // Act
        UserProfile updatedProfile = userProfileService.updateUserProfile(userId, dto);

        // Assert
        assertNotNull(updatedProfile);
        assertEquals("http://example.com/pic.jpg", updatedProfile.getProfilePictureUrl());
        verify(userCredentialsRepository, times(1)).save(user);
        verify(userProfileRepository, times(1)).save(userProfile);
    }

    @Test
    void getUserProfileById() {
        // Arrange
        int userId = 1;
        UserProfile userProfile = new UserProfile();
        when(userProfileRepository.findByUserCredentialsId(userId)).thenReturn(userProfile);

        // Act
        UserProfile foundProfile = userProfileService.getUserProfileById(userId);

        // Assert
        assertNotNull(foundProfile);
        assertEquals(userProfile, foundProfile);
    }

    @Test
    void getTotalUpvotesByUser() {
        // Arrange
        String username = "testUser";
        when(postRepository.sumUpvotesForPostsByUser(username)).thenReturn(5L);
        when(answerRepository.sumUpvotesForAnswersByUser(username)).thenReturn(3L);

        // Act
        Long totalUpvotes = userProfileService.getTotalUpvotesByUser(username);

        // Assert
        assertEquals(8, totalUpvotes);
    }

    @Test
    void getTotalDownvotesByUser() {
        // Arrange
        String username = "testUser";
        when(postRepository.sumDownvotesForPostsByUser(username)).thenReturn(2L);
        when(answerRepository.sumDownvotesForAnswersByUser(username)).thenReturn(4L);

        // Act
        Long totalDownvotes = userProfileService.getTotalDownvotesByUser(username);

        // Assert
        assertEquals(6, totalDownvotes);
    }

    @Test
    void countFollowedPostsByUsername() {
        // Arrange
        String username = "testUser";
        when(postRepository.countFollowedPostsByUser(username)).thenReturn(10L);

        // Act
        Long followedPostsCount = userProfileService.countFollowedPostsByUsername(username);

        // Assert
        assertEquals(10, followedPostsCount);
    }

    @Test
    void countPostsByUserId() {
        // Arrange
        int userId = 1;
        when(postRepository.countByUser_Id(userId)).thenReturn(5L);

        // Act
        Long postCount = userProfileService.countPostsByUserId(userId);

        // Assert
        assertEquals(5, postCount);
    }

    @Test
    void countAnswersByUserId() {
        // Arrange
        int userId = 1;
        when(answerRepository.countAnswersByUserId(userId)).thenReturn(3L);

        // Act
        Long answerCount = userProfileService.countAnswersByUserId(userId);

        // Assert
        assertEquals(3, answerCount);
    }

    @Test
    void getProfilePictureByUsername_UserFound() {
        // Arrange
        String username = "testUser";
        UserCredentials userCredentials = new UserCredentials();
        UserProfile userProfile = new UserProfile();
        userProfile.setProfilePictureUrl("http://example.com/pic.jpg");
        userCredentials.setUserProfile(userProfile);

        when(userCredentialsRepository.findByUsername(username)).thenReturn(Optional.of(userCredentials));

        // Act
        String profilePictureUrl = userProfileService.getProfilePictureByUsername(username);

        // Assert
        assertEquals("http://example.com/pic.jpg", profilePictureUrl);
    }

    @Test
    void getProfilePictureByUsername_UserNotFound() {
        // Arrange
        String username = "nonExistentUser";
        when(userCredentialsRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userProfileService.getProfilePictureByUsername(username);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void getUserIdByUsername_UserFound() {
        // Arrange
        String username = "testUser";
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setId(1);
        when(userCredentialsRepository.findByUsername(username)).thenReturn(Optional.of(userCredentials));

        // Act
        Integer userId = userProfileService.getUserIdByUsername(username);

        // Assert
        assertEquals(1, userId);
    }

    @Test
    void getUserIdByUsername_UserNotFound() {
        // Arrange
        String username = "nonExistentUser";
        when(userCredentialsRepository.findByUsername(username)).thenReturn(Optional.empty());

        // Act
        Integer userId = userProfileService.getUserIdByUsername(username);

        // Assert
        assertNull(userId);
    }
}
