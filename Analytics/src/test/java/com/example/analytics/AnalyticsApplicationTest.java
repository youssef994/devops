package com.example.analytics;
import com.example.analytics.Repository.PlatformStatisticsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AnalyticsApplicationTest {
    @MockBean
    private PlatformStatisticsRepository platformStatisticsRepository;
    @Test
    void contextLoads() {

    }
}
