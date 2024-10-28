package com.example.analytics.Service;


import com.example.analytics.dto.UserStatisticsDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class QuestionAnswerService {

    @Autowired
    private RestTemplate analyticsRestTemplate;

    public UserStatisticsDTO getUserStatistics(int userId) {
        String url = "http://QUESTION-ANSWER-SERVICE/api/user/all/" + userId;
        UserStatisticsDTO stats = analyticsRestTemplate.getForObject(url, UserStatisticsDTO.class);
        if (stats == null) {
            throw new RuntimeException("Failed to fetch user statistics");
        }
        return stats;
    }
}
