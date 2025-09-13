package com.crediya.secutiry;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@AllArgsConstructor
@Configuration
public class SecurityBeansConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityBeansConfig.class);

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtUtil jwtUtil) {
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtil);
        LOGGER.debug("Entering securityWebFilterChain - ServerHttpSecurity :{}, JwtUtil: {}", http, jwtUtil );

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.POST, "/api/v1/login").permitAll()
                        .pathMatchers(HttpMethod.POST,"/api/v1/solicitudes").hasAuthority("ROLE_CLIENT")
                        .pathMatchers(HttpMethod.GET, "/api/v1/solicitudes").hasAuthority("ROLE_ADVISOR")
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @PostConstruct
    public void init() {
        LOGGER.info("✅ SecurityBeansConfig cargada!");
    }

}
