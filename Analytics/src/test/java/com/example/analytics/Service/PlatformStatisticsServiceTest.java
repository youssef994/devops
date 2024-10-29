package com.example.analytics.Service;


import com.example.analytics.Model.PlatformStatistics;
import com.example.analytics.Repository.PlatformStatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PlatformStatisticsServiceTest {

    @InjectMocks
    private PlatformStatisticsService platformStatisticsService;

    @Mock
    @Qualifier("analyticsRestTemplate")
    private RestTemplate restTemplate;

    @Mock
    private PlatformStatisticsRepository platformStatisticsRepository;

    private PlatformStatistics stats;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        stats = new PlatformStatistics();
        stats.setTotalUsers(100L);
        stats.setActiveUsers(80L);
        stats.setNewUsersToday(5L);
        stats.setNewUsersThisMonth(30L);
        stats.setCreatedDate(LocalDateTime.now());
    }

    @Test
    void testUpdatePlatformStatistics() {
        when(restTemplate.getForObject(any(String.class), eq(Long.class))).thenReturn(100L, 80L, 5L, 30L);
        when(platformStatisticsRepository.findTopByOrderByIdDesc()).thenReturn(stats);

        platformStatisticsService.updatePlatformStatistics();

        verify(platformStatisticsRepository, times(1)).deleteByCreatedDateBetween(any(), any());
        verify(platformStatisticsRepository, times(1)).save(any(PlatformStatistics.class));
    }

    @Test
    void testGetLatestStatistics() {
        when(platformStatisticsRepository.findTopByOrderByIdDesc()).thenReturn(stats);

        PlatformStatistics latestStats = platformStatisticsService.getLatestStatistics();

        assertNotNull(latestStats);
        assertEquals(stats.getTotalUsers(), latestStats.getTotalUsers());
    }

    @Test
    void testGetAllStatistics() {
        when(platformStatisticsRepository.findAllByOrderByCreatedDateAsc()).thenReturn(Collections.singletonList(stats));

        var allStats = platformStatisticsService.getAllStatistics();

        assertNotNull(allStats);
        assertEquals(1, allStats.size());
        assertEquals(stats.getTotalUsers(), allStats.get(0).getTotalUsers());
    }

    @Test
    void testGetDayStatistics() {
        when(platformStatisticsRepository.findAll()).thenReturn(Collections.singletonList(stats));

        var dayStats = platformStatisticsService.getDayStatistics();

        assertNotNull(dayStats);
        assertEquals(1, dayStats.size());
    }

    @Test
    void testGetStatisticsByDate() {
        LocalDate date = LocalDate.now();
        when(platformStatisticsRepository.findAll()).thenReturn(Collections.singletonList(stats));

        var statisticsByDate = platformStatisticsService.getStatisticsByDate(date);

        assertNotNull(statisticsByDate);
        assertEquals(1, statisticsByDate.size());
    }
}
