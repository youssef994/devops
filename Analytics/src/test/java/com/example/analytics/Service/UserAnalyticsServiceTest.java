package com.example.analytics.Service;


import com.example.analytics.Model.UserAnalytics;
import com.example.analytics.Repository.UserAnalyticsRepository;
import com.example.analytics.dto.UserAnalyticsSummary;
import com.example.analytics.dto.UserCredentialsDTO;
import com.example.analytics.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserAnalyticsServiceTest {

    @InjectMocks
    private UserAnalyticsService userAnalyticsService;

    @Mock
    private RestTemplate analyticsRestTemplate;

    @Mock
    private UserAnalyticsRepository userAnalyticsRepository;

    private UserAnalytics userAnalytics;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userAnalytics = new UserAnalytics();
        userAnalytics.setUserId(1L);
        userAnalytics.setUsername("testuser");
        userAnalytics.setLastLoginTime(LocalDateTime.now());
        userAnalytics.setTotalPosts(5);
        userAnalytics.setTotalAnswers(10);
        userAnalytics.setTotalUpvotes(15);
        userAnalytics.setTotalDownvotes(2);
        userAnalytics.setFollowersCount(3);
    }



    @Test
    void testGetUsernameById() {
        UserResponse userResponse = new UserResponse();
        userResponse.setUsername("testuser");

        when(analyticsRestTemplate.getForObject("http://localhost:8088/api/auth/1", UserResponse.class))
                .thenReturn(userResponse);

        String username = userAnalyticsService.getUsernameById(1L);

        assertEquals("testuser", username);
    }

    @Test
    void testGetUserAnalyticsSummary() {
        when(userAnalyticsRepository.findAll()).thenReturn(Arrays.asList(userAnalytics));

        UserAnalyticsSummary summary = userAnalyticsService.getUserAnalyticsSummary();

        assertNotNull(summary);
        assertEquals(1, summary.getTotalUsers());
        assertEquals(5, summary.getTotalPosts());
        assertEquals(10, summary.getTotalAnswers());
        assertEquals(15, summary.getTotalUpvotes());
        assertEquals(2, summary.getTotalDownvotes());
        assertEquals(3, summary.getTotalFollowers());
    }
}
