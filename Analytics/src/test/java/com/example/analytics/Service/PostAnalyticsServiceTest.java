package com.example.analytics.Service;


import com.example.analytics.Model.PostAnalytics;
import com.example.analytics.Repository.PostAnalyticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostAnalyticsServiceTest {

    @InjectMocks
    private PostAnalyticsService postAnalyticsService;

    @Mock
    private PostAnalyticsRepository postAnalyticsRepository;

    private PostAnalytics postAnalytics;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postAnalytics = new PostAnalytics();
        postAnalytics.setId(1L);
        postAnalytics.setPostId(1L);
        postAnalytics.setViewCount(100);

    }

    @Test
    void testGetAllPostAnalytics() {
        when(postAnalyticsRepository.findAll()).thenReturn(Collections.singletonList(postAnalytics));

        var allPostAnalytics = postAnalyticsService.getAllPostAnalytics();

        assertNotNull(allPostAnalytics);
        assertEquals(1, allPostAnalytics.size());
        assertEquals(postAnalytics.getId(), allPostAnalytics.get(0).getId());
    }

    @Test
    void testGetPostAnalyticsById() {
        when(postAnalyticsRepository.findById(1L)).thenReturn(Optional.of(postAnalytics));

        PostAnalytics foundPostAnalytics = postAnalyticsService.getPostAnalyticsById(1L);

        assertNotNull(foundPostAnalytics);
        assertEquals(postAnalytics.getId(), foundPostAnalytics.getId());
    }

    @Test
    void testGetPostAnalyticsById_NotFound() {
        when(postAnalyticsRepository.findById(1L)).thenReturn(Optional.empty());

        PostAnalytics foundPostAnalytics = postAnalyticsService.getPostAnalyticsById(1L);

        assertNull(foundPostAnalytics);
    }

    @Test
    void testSavePostAnalytics() {
        when(postAnalyticsRepository.save(any(PostAnalytics.class))).thenReturn(postAnalytics);

        PostAnalytics savedPostAnalytics = postAnalyticsService.savePostAnalytics(postAnalytics);

        assertNotNull(savedPostAnalytics);
        assertEquals(postAnalytics.getId(), savedPostAnalytics.getId());
    }
}
