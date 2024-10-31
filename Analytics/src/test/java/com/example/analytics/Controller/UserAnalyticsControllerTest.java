package com.example.analytics.Controller;

import com.example.analytics.Model.UserAnalytics;
import com.example.analytics.Repository.UserAnalyticsRepository;
import com.example.analytics.Service.UserAnalyticsService;
import com.example.analytics.dto.UserAnalyticsSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserAnalyticsController.class)
class UserAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAnalyticsService userAnalyticsService;

    @MockBean
    private UserAnalyticsRepository userAnalyticsRepository;

    private UserAnalytics sampleUserAnalytics;

    @BeforeEach
    void setup() {
        sampleUserAnalytics = new UserAnalytics();
        sampleUserAnalytics.setId(1L);
        sampleUserAnalytics.setUserId(100L);
        sampleUserAnalytics.setUsername("testUser");
        sampleUserAnalytics.setLoginCount(10);
        sampleUserAnalytics.setLastLoginTime(LocalDateTime.now());
        sampleUserAnalytics.setTotalPosts(5);
        sampleUserAnalytics.setTotalAnswers(3);
        sampleUserAnalytics.setTotalUpvotes(15);
        sampleUserAnalytics.setTotalDownvotes(2);
        sampleUserAnalytics.setFollowersCount(20);
        sampleUserAnalytics.setCreatedDate(LocalDate.now());
    }

    @Test
    void testGetUserAnalytics() throws Exception {
        when(userAnalyticsRepository.findByUserId(100L)).thenReturn(Optional.of(sampleUserAnalytics));

        mockMvc.perform(get("/analytics/user/{userId}", 100L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"))
                .andExpect(jsonPath("$.loginCount").value(10));
    }

    @Test
    void testGetUserAnalyticsNotFound() throws Exception {
        when(userAnalyticsRepository.findByUserId(200L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/analytics/user/{userId}", 200L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(nullValue())); // Expecting null for the id
    }


    @Test
    void testGetAllUserAnalytics() throws Exception {
        List<UserAnalytics> userAnalyticsList = Arrays.asList(sampleUserAnalytics, sampleUserAnalytics);
        when(userAnalyticsRepository.findAll()).thenReturn(userAnalyticsList);

        mockMvc.perform(get("/analytics/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

}
