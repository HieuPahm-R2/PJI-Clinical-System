package com.vietnam.pji.config.auth;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfigure {
    @Value("${app.cors.allowed-origin-patterns:http://localhost:3000,http://localhost:4173,http://localhost:5173}")
    private String allowedOriginPatterns;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> origins = Arrays.stream(allowedOriginPatterns.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList();

        configuration.setAllowedOriginPatterns(origins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed
        // methods
        configuration.setAllowedHeaders(
                Arrays.asList("Authorization", "Content-Type", "Accept", "x-no-retry", "Range"));
        configuration.setExposedHeaders(
                Arrays.asList("Content-Range", "Accept-Ranges", "Cache-Control", "Pragma", "Expires")); // Expose
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        // How long the response from a pre-flight request can be cached by clients
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this configuration to all paths

        return source;
    }
}
