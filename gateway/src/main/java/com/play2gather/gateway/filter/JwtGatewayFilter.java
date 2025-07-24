package com.play2gather.gateway.filter;

import com.play2gather.gateway.config.JwtService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Order(1)
public class JwtGatewayFilter implements GlobalFilter {

    private static final Logger log = LogManager.getLogger(JwtGatewayFilter.class);

    @Autowired
    private JwtService jwtService;

    // Endpoints públicos que não exigem autenticação
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/login", "/register", "/refresh", "/logout"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Se for um endpoint público, segue direto
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Se não houver token, segue mesmo assim — downstream decide se é necessário
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("ENTROU AQUI");
            return chain.filter(exchange);
        }

        try {
            String token = authHeader.substring(7);
            Long userId = jwtService.getIdFromAccessToken(token);
            String email = jwtService.getEmailFromAccessToken(token);
            List<String> roles = jwtService.getRolesFromAccessToken(token);

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-User-Email", email)
                    .header("X-User-Roles", String.join(",", roles))
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());

        } catch (Exception e) {
            log.error("e: ", e);
            return unauthorized(exchange.getResponse());
        }
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> unauthorized(ServerHttpResponse response) {
        response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }
}
