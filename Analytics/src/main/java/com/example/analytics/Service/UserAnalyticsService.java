package com.example.analytics.Service;

import com.example.analytics.Model.UserAnalytics;
import com.example.analytics.Repository.UserAnalyticsRepository;
import com.example.analytics.dto.UserAnalyticsSummary;
import com.example.analytics.dto.UserCredentialsDTO;


import com.example.analytics.dto.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class UserAnalyticsService {

    @Autowired
    private RestTemplate analyticsRestTemplate;

    @Autowired
    private UserAnalyticsRepository userAnalyticsRepository;

    private static final String USER_SERVICE_URL = "http://localhost:8088/api/auth/";

    @Scheduled(cron = "*/10 * * * * *")
    public void updateAnalytics() {
        // Fetch user credentials from the user service
        UserCredentialsDTO[] userCredentialsArray = analyticsRestTemplate.getForObject(
                "http://localhost:8088/api/auth/AllUsers",
                UserCredentialsDTO[].class
        );

        if (userCredentialsArray != null) {
            for (UserCredentialsDTO userCredentials : userCredentialsArray) {
                // Update or create user analytics
                UserAnalytics userAnalytics = userAnalyticsRepository.findByUserId(userCredentials.getId())
                        .orElse(new UserAnalytics());
                userAnalytics.setUserId(userCredentials.getId());
                userAnalytics.setUsername(userCredentials.getUsername()); // Set username
                userAnalytics.setLastLoginTime(userCredentials.getLastLoginDate());
                userAnalytics.setCreatedDate(userCredentials.getCreatedDate());
                userAnalytics.setLoginCount(userCredentials.getLoginCount()); // Set login count

                // Fetch additional statistics
                userAnalytics.setTotalPosts(fetchUserPostsCount(userCredentials.getId()));
                userAnalytics.setTotalAnswers(fetchUserAnswersCount(userCredentials.getId()));
                userAnalytics.setTotalUpvotes(fetchUserUpvotesCount(userCredentials.getId()));
                userAnalytics.setTotalDownvotes(fetchUserDownvotesCount(userCredentials.getId()));
                userAnalytics.setFollowersCount(fetchUserFollowedCount(userCredentials.getId()));

                userAnalyticsRepository.save(userAnalytics);
            }
        }
    }




    private int fetchUserPostsCount(Long userId) {
        String url = "http://localhost:8088/api/posts/count/by-user?userId=" + userId;
        return analyticsRestTemplate.getForObject(url, Integer.class);
    }

    private int fetchUserAnswersCount(Long userId) {
        String url = "http://localhost:8088/api/answers/count?userId=" + userId;
        return analyticsRestTemplate.getForObject(url, Integer.class);
    }

    private int fetchUserUpvotesCount(Long userId) {
        String url = "http://localhost:8088/api/posts/upvotes/count?username=" + getUsernameById(userId);
        return analyticsRestTemplate.getForObject(url, Integer.class);
    }

    private int fetchUserDownvotesCount(Long userId) {
        String url = "http://localhost:8088/api/posts/downvotes/count?username=" + getUsernameById(userId);
        return analyticsRestTemplate.getForObject(url, Integer.class);
    }

    private int fetchUserFollowedCount(Long userId) {
        String url = "http://localhost:8088/api/posts/followed/count?username=" + getUsernameById(userId);
        return analyticsRestTemplate.getForObject(url, Integer.class);
    }


    public String getUsernameById(Long userId) {
        String url = USER_SERVICE_URL + userId; // Endpoint to get user details
        try {
            UserResponse userResponse = analyticsRestTemplate.getForObject(url, UserResponse.class);
            if (userResponse != null) {
                return userResponse.getUsername(); // Assuming UserResponse has getUsername method
            }
        } catch (Exception e) {
            // Handle the exception properly
            e.printStackTrace();
        }
        return ""; // Return an empty string or handle as appropriate
    }
    public UserAnalyticsSummary getUserAnalyticsSummary() {
        List<UserAnalytics> allAnalytics = userAnalyticsRepository.findAll();

        int totalUsers = allAnalytics.size();
        int totalPosts = allAnalytics.stream().mapToInt(UserAnalytics::getTotalPosts).sum();
        int totalAnswers = allAnalytics.stream().mapToInt(UserAnalytics::getTotalAnswers).sum();
        int totalUpvotes = allAnalytics.stream().mapToInt(UserAnalytics::getTotalUpvotes).sum();
        int totalDownvotes = allAnalytics.stream().mapToInt(UserAnalytics::getTotalDownvotes).sum();
        int totalFollowers = allAnalytics.stream().mapToInt(UserAnalytics::getFollowersCount).sum();

        return new UserAnalyticsSummary(totalUsers, totalPosts, totalAnswers, totalUpvotes, totalDownvotes, totalFollowers);
    }
}