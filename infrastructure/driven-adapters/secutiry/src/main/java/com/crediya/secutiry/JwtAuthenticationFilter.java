package com.crediya.secutiry;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

@AllArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            /*No token: continuar sin autenticación (pero SecurityWebFilterChain decidirá si lo bloquea)
            return chain.filter(exchange);*/
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization header"));
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.validateToken(token);
            String email = claims.getSubject();

            // Manejo flexible del campo "role"
            Object roleObj = claims.get("role");
            int role = (roleObj instanceof Number)
                    ? ((Number) roleObj).intValue()
                    : Integer.parseInt(roleObj.toString());

            SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                    switch (role) {
                        case 1 -> "ROLE_ADMINISTRATOR";
                        case 2 -> "ROLE_CLIENT";
                        case 3 -> "ROLE_ADVISOR";
                        default -> "ROLE_UNKNOWN";
                    }
            );

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    email,
                    null,
                    Collections.singleton(authority)
            );

            return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));

        } catch (Exception e) {
            // Devuelve 401 en lugar de 500
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid JWT token", e));
        }
    }
}