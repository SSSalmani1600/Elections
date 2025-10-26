package nl.hva.election_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // welke routes mogen zonder login
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/next-elections", "/api/auth/**").permitAll()
                        .anyRequest().authenticated()
                )
                // frontend kan gewoon requests sturen
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {});

        return http.build();
    }
}
