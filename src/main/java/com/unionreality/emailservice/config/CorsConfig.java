package com.unionreality.emailservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    CorsFilter corsFilter(EmailServiceProperties emailServiceProperties) {
        String allowedOrigins = emailServiceProperties.corsAllowedOrigins();
        if (allowedOrigins == null || allowedOrigins.isBlank()) {
            allowedOrigins = "*";
        }
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        if (origins.size() == 1 && "*".equals(origins.get(0))) {
            config.addAllowedOriginPattern("*");
        } else {
            config.setAllowedOrigins(origins);
        }
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
