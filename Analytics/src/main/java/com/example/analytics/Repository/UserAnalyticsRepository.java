package com.example.analytics.Repository;



import com.example.analytics.Model.UserAnalytics;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserAnalyticsRepository extends JpaRepository<UserAnalytics, Long> {

    Optional<UserAnalytics> findByUserId(Long userId);

}
