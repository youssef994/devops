package com.example.questionanwser.Controller;

import com.example.questionanwser.Model.UserProfile;
import com.example.questionanwser.Service.UserProfileService;
import dto.UpdateUserProfileDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UserProfileControllerTest {

    @InjectMocks
    private UserProfileController userProfileController;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateUserProfile() throws IOException {
        int userId = 1;
        UpdateUserProfileDTO updateUserProfileDTO = new UpdateUserProfileDTO();
        UserProfile updatedUserProfile = new UserProfile();

        when(userProfileService.updateUserProfile(eq(userId), any(UpdateUserProfileDTO.class)))
                .thenReturn(updatedUserProfile);
        when(mockFile.getOriginalFilename()).thenReturn("profile.png");
        when(mockFile.getBytes()).thenReturn(new byte[0]);
        when(mockFile.isEmpty()).thenReturn(false);

        ResponseEntity<UserProfile> response = userProfileController.updateUserProfile(userId, updateUserProfileDTO, mockFile);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedUserProfile, response.getBody());
        verify(userProfileService).updateUserProfile(userId, updateUserProfileDTO);
    }

    @Test
    void testGetUserProfile() {
        int userId = 1;
        UserProfile userProfile = new UserProfile();

        when(userProfileService.getUserProfileById(userId)).thenReturn(userProfile);

        ResponseEntity<UserProfile> response = userProfileController.getUserProfile(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userProfile, response.getBody());
        verify(userProfileService).getUserProfileById(userId);
    }

    @Test
    void testGetAllStatisticsByUserId() {
        int userId = 1;
        String username = "testUser";
        long upvotes = 10L;
        long downvotes = 5L;
        long followedPosts = 3L;
        long posts = 15L;
        long answers = 8L;

        when(userProfileService.getTotalUpvotesByUser(username)).thenReturn(upvotes);
        when(userProfileService.getTotalDownvotesByUser(username)).thenReturn(downvotes);
        when(userProfileService.countFollowedPostsByUsername(username)).thenReturn(followedPosts);
        when(userProfileService.countPostsByUserId(userId)).thenReturn(posts);
        when(userProfileService.countAnswersByUserId(userId)).thenReturn(answers);

        UserProfileController.UserStatistics statistics = userProfileController.getAllStatisticsByUserId(userId, username);

        assertEquals(upvotes, statistics.upvotes);
        assertEquals(downvotes, statistics.downvotes);
        assertEquals(followedPosts, statistics.followedPosts);
        assertEquals(posts, statistics.posts);
        assertEquals(answers, statistics.answers);
    }

    @Test
    void testGetProfilePictureByUsername_UserFound() {
        String username = "testUser";
        String profilePictureUrl = "http://example.com/profile.png";

        when(userProfileService.getProfilePictureByUsername(username)).thenReturn(profilePictureUrl);

        ResponseEntity<String> response = userProfileController.getProfilePictureByUsername(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(profilePictureUrl, response.getBody());
        verify(userProfileService).getProfilePictureByUsername(username);
    }

    @Test
    void testGetProfilePictureByUsername_UserNotFound() {
        String username = "unknownUser";

        when(userProfileService.getProfilePictureByUsername(username)).thenReturn(null);

        ResponseEntity<String> response = userProfileController.getProfilePictureByUsername(username);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userProfileService).getProfilePictureByUsername(username);
    }

    @Test
    void testGetUserIdByUsername_UserFound() {
        String username = "testUser";
        int userId = 1;

        when(userProfileService.getUserIdByUsername(username)).thenReturn(userId);

        ResponseEntity<Integer> response = userProfileController.getUserIdByUsername(username);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userId, response.getBody());
        verify(userProfileService).getUserIdByUsername(username);
    }

    @Test
    void testGetUserIdByUsername_UserNotFound() {
        String username = "unknownUser";

        when(userProfileService.getUserIdByUsername(username)).thenReturn(null);

        ResponseEntity<Integer> response = userProfileController.getUserIdByUsername(username);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userProfileService).getUserIdByUsername(username);
    }
}
