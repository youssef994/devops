package com.example.analytics.Repository;

import com.example.analytics.Model.PlatformStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PlatformStatisticsRepository extends JpaRepository<PlatformStatistics, Long> {
    PlatformStatistics findTopByOrderByIdDesc();

    List<PlatformStatistics> findAllByOrderByCreatedDateAsc();




    void deleteByCreatedDateBetween(LocalDateTime start, LocalDateTime end);


}