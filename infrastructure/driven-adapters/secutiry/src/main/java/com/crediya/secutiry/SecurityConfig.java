package com.crediya.secutiry;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import javax.crypto.spec.SecretKeySpec;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    public static final Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);


    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter) {
        LOGGER.debug("Entering to securityWebFilterChain - ServerHttpSecurity: {}", http);
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.POST, "/api/v1/login").permitAll()
                        .pathMatchers(HttpMethod.POST,"/api/v1/solicitudes").hasRole("CLIENT")
                        .pathMatchers(HttpMethod.GET, "/api/v1/solicitudes").hasAnyRole("ADVISOR", "ADMINISTRATOR")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/solicitudes").hasAnyRole("ADVISOR")
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                )
                .build();
    }

    @Bean
    public static ReactiveJwtAuthenticationConverterAdapter jwtAuthenticationConverter() {
        LOGGER.debug("Entering to jwtAuthenticationConverter");

        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("role");

        Converter<Jwt, Collection<GrantedAuthority>> loggingConverter = jwt -> {
            LOGGER.info("JWT Claims received: {}", jwt.getClaims());

            Object roleObj = jwt.getClaim("role");
            String roleName = switch (roleObj instanceof Number n ? n.intValue(): Integer.parseInt(roleObj.toString())){
                case 1 -> "ROLE_ADMINISTRATOR";
                case 2 -> "ROLE_CLIENT";
                case 3 -> "ROLE_ADVISOR";
                default -> "ROLE_UNKNOWN";
            };
            List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(roleName));
            LOGGER.info("Authorities mapped: {}", authorities);
            return authorities;
        };

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(loggingConverter);

        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
    @Bean
    public ReactiveJwtDecoder jwtDecoder(
            @Value("${spring.security.oauth2.resourceserver.jwt.secret-key}") String secret) {
            LOGGER.debug("Entering to jwtDecoder - secret: {}", secret);
        return NimbusReactiveJwtDecoder.withSecretKey(
                new SecretKeySpec(secret.getBytes(), "HmacSHA256")
        ).build();
    }
}

