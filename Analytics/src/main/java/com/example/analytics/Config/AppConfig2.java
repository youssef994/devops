package com.example.analytics.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig2 {
    @Bean
    public RestTemplate analyticsRestTemplate() {
        return new RestTemplate();
    }
}

