package com.example.analytics.Controller;

import com.example.analytics.Model.PlatformStatistics;
import com.example.analytics.Service.PlatformStatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlatformStatisticsController.class)
class PlatformStatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlatformStatisticsService platformStatisticsService;

    private PlatformStatistics sampleStatistic;

    @BeforeEach
    void setup() {
        sampleStatistic = new PlatformStatistics();
        sampleStatistic.setId(1L);
        sampleStatistic.setActiveUsers(5);
        sampleStatistic.setNewUsersToday(10); // Use newUsersToday here
        sampleStatistic.setNewUsersThisMonth(15); // Optional, add if needed
    }

    @Test
    void testGetPlatformStatistics() throws Exception {
        when(platformStatisticsService.getLatestStatistics()).thenReturn(sampleStatistic);

        mockMvc.perform(get("/analytics/platform-statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.newUsersToday").value(10)) // Change to newUsersToday
                .andExpect(jsonPath("$.activeUsers").value(5));
    }

    @Test
    void testGetAllStatistics() throws Exception {
        List<PlatformStatistics> statsList = Arrays.asList(sampleStatistic, sampleStatistic);
        when(platformStatisticsService.getAllStatistics()).thenReturn(statsList);

        mockMvc.perform(get("/analytics/platform-statistics/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetAllDayStatistics() throws Exception {
        List<PlatformStatistics> statsList = Arrays.asList(sampleStatistic, sampleStatistic);
        when(platformStatisticsService.getDayStatistics()).thenReturn(statsList);

        mockMvc.perform(get("/analytics/platform-statistics/day"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetDayPlatformStatistics() throws Exception {
        List<PlatformStatistics> statsList = Arrays.asList(sampleStatistic);
        LocalDate testDate = LocalDate.of(2023, 10, 25);
        when(platformStatisticsService.getStatisticsByDate(testDate)).thenReturn(statsList);

        mockMvc.perform(get("/analytics/platform-statistics/NewUsersday")
                        .param("date", "2023-10-25")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].newUsersToday").value(10)); // Change to newUsersToday
    }
}
