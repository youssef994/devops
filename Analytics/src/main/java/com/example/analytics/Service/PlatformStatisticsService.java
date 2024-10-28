package com.example.analytics.Service;

import com.example.analytics.Model.PlatformStatistics;
import com.example.analytics.Repository.PlatformStatisticsRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PlatformStatisticsService {

    @Autowired
    @Qualifier("analyticsRestTemplate")
    private RestTemplate restTemplate;

    @Autowired
    private PlatformStatisticsRepository platformStatisticsRepository;

    private static final Logger logger = LoggerFactory.getLogger(PlatformStatisticsService.class);

    @Scheduled(cron = "0 */30 * * * *")
    @Transactional
    public void updatePlatformStatistics() {
        logger.info("Scheduled task started to update platform statistics.");

        long totalUsers = restTemplate.getForObject("http://localhost:8080/api/auth/countTotalUsers", Long.class);
        long activeUsers = restTemplate.getForObject("http://localhost:8080/api/auth/countActiveUsers", Long.class);
        long newUsersToday = restTemplate.getForObject("http://localhost:8080/api/auth/countNewUsersToday", Long.class);
        long newUsersThisMonth = restTemplate.getForObject("http://localhost:8080/api/auth/countNewUsersThisMonth", Long.class);


        // Determine the current day
        LocalDate today = LocalDate.now();

        // Delete all old statistics for the current day
        platformStatisticsRepository.deleteByCreatedDateBetween(
                today.atStartOfDay(), today.plusDays(1).atStartOfDay());

        // Save the new statistics
        PlatformStatistics stats = new PlatformStatistics();
        stats.setTotalUsers(totalUsers);
        stats.setActiveUsers(activeUsers);
        stats.setNewUsersToday(newUsersToday);
        stats.setNewUsersThisMonth(newUsersThisMonth);
        stats.setCreatedDate(LocalDateTime.now());
        platformStatisticsRepository.save(stats);

        logger.info("Platform statistics updated: Total Users={}, Active Users={}, New Users Today={}, New Users This Month={}",
                totalUsers, activeUsers, newUsersToday, newUsersThisMonth);
    }

    public PlatformStatistics getLatestStatistics() {
        return platformStatisticsRepository.findTopByOrderByIdDesc();
    }

    public List<PlatformStatistics> getAllStatistics() {
        return platformStatisticsRepository.findAllByOrderByCreatedDateAsc();
    }

    public List<PlatformStatistics> getDayStatistics() {
        // Fetch all statistics
        List<PlatformStatistics> allStats = platformStatisticsRepository.findAll();

        // Map to hold the latest statistics by date
        Map<LocalDate, PlatformStatistics> latestStatsByDate = new HashMap<>();

        for (PlatformStatistics stats : allStats) {
            LocalDate date = stats.getCreatedDate().toLocalDate();
            PlatformStatistics existingStat = latestStatsByDate.get(date);
            if (existingStat == null || stats.getCreatedDate().isAfter(existingStat.getCreatedDate())) {
                latestStatsByDate.put(date, stats);
            }
        }

        return new ArrayList<>(latestStatsByDate.values());
    }
    public List<PlatformStatistics> getStatisticsByDate(LocalDate date) {
        // Fetch all statistics
        List<PlatformStatistics> allStats = platformStatisticsRepository.findAll();

        // Map to hold the latest statistics by date
        Map<LocalDate, PlatformStatistics> latestStatsByDate = new HashMap<>();

        for (PlatformStatistics stats : allStats) {
            LocalDate statDate = stats.getCreatedDate().toLocalDate();
            if (statDate.equals(date)) {
                if (!latestStatsByDate.containsKey(statDate) || stats.getCreatedDate().isAfter(latestStatsByDate.get(statDate).getCreatedDate())) {
                    latestStatsByDate.put(statDate, stats);
                }
            }
        }

        return new ArrayList<>(latestStatsByDate.values());
    }

}
