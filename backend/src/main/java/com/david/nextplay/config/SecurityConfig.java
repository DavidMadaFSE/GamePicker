package com.david.nextplay.config;

import static org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher.pathPattern;

import com.david.nextplay.filter.JwtAuthenticationFilter;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final String allowedOrigins;

    SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            @Value("${app.cors.allowed-origins}") String allowedOrigins) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.allowedOrigins = allowedOrigins;
    }

    @Bean
    public PasswordEncoder PasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OpenAPI nextPlayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NextPlay API")
                        .description("Backend API for game discovery, reviews, libraries, and recommendations")
                        .version("1.0.0"));
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Allow swagger through security
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html")
                        .permitAll()

                        .requestMatchers(pathPattern("/api/auth/**")).permitAll()
                        .requestMatchers(pathPattern("/api/health")).permitAll()
                        .requestMatchers(pathPattern("/api/health/**")).permitAll()

                        // Public game routes
                        .requestMatchers(pathPattern(HttpMethod.GET, "/api/games")).permitAll()
                        .requestMatchers(pathPattern(HttpMethod.GET, "/api/games/**")).permitAll()
                        .requestMatchers(pathPattern(HttpMethod.GET, "/api/games/{gameId}/reviews")).permitAll()

                        // Review routes
                        .requestMatchers(pathPattern(HttpMethod.POST, "/api/games/{gameId}/reviews")).authenticated()
                        .requestMatchers(pathPattern(HttpMethod.GET, "/api/users/me/reviews")).authenticated()
                        .requestMatchers(pathPattern(HttpMethod.PATCH, "/api/reviews/{id}")).authenticated()
                        .requestMatchers(pathPattern(HttpMethod.DELETE, "/api/reviews/{id}")).authenticated()

                        // User routes
                        .requestMatchers(pathPattern(HttpMethod.GET, "/api/users/me")).authenticated()

                        // Library routes
                        .requestMatchers("/api/library/**").authenticated()

                        // Recommendation routes
                        .requestMatchers("/api/recommendations/**").authenticated()

                        // Admin game routes
                        .requestMatchers(HttpMethod.POST, "/api/games/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/games/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/games/**").hasRole("ADMIN")

                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
