package com.example.analytics.Controller;

import com.example.analytics.Model.PostAnalytics;
import com.example.analytics.Service.PostAnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostAnalyticsController.class)
class PostAnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostAnalyticsService postAnalyticsService;

    private PostAnalytics samplePostAnalytics;

    @BeforeEach
    void setup() {
        samplePostAnalytics = new PostAnalytics();
        samplePostAnalytics.setId(1L);
        samplePostAnalytics.setPostId(100L);
        samplePostAnalytics.setViewCount(200);
        samplePostAnalytics.setLikeCount(50);
        samplePostAnalytics.setLastViewedTime(LocalDateTime.now());
        samplePostAnalytics.setCommentsCount(10);
        samplePostAnalytics.setUpvotesCount(20);
        samplePostAnalytics.setDownvotesCount(5);
    }

    @Test
    void testGetAllPostAnalytics() throws Exception {
        List<PostAnalytics> postAnalyticsList = Arrays.asList(samplePostAnalytics, samplePostAnalytics);
        when(postAnalyticsService.getAllPostAnalytics()).thenReturn(postAnalyticsList);

        mockMvc.perform(get("/analytics/post-analytics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetPostAnalyticsById() throws Exception {
        when(postAnalyticsService.getPostAnalyticsById(1L)).thenReturn(samplePostAnalytics);

        mockMvc.perform(get("/analytics/post-analytics/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(100L))
                .andExpect(jsonPath("$.viewCount").value(200));
    }

    @Test
    void testSavePostAnalytics() throws Exception {
        PostAnalytics newPostAnalytics = new PostAnalytics();
        newPostAnalytics.setPostId(101L);
        newPostAnalytics.setViewCount(150);
        newPostAnalytics.setLikeCount(30);
        newPostAnalytics.setCommentsCount(5);
        newPostAnalytics.setUpvotesCount(12);
        newPostAnalytics.setDownvotesCount(3);

        when(postAnalyticsService.savePostAnalytics(newPostAnalytics)).thenReturn(samplePostAnalytics);

        mockMvc.perform(post("/analytics/post-analytics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"postId\":101,\"viewCount\":150,\"likeCount\":30,\"commentsCount\":5,\"upvotesCount\":12,\"downvotesCount\":3}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(100L)); // Expecting the samplePostAnalytics return
    }
}
