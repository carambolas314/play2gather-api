package com.play2gather.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Order(-2)
@Slf4j
public class TracingFilter implements GlobalFilter{

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = UUID.randomUUID().toString();

        ServerHttpRequest mutatedRequest = exchange.getRequest()
                .mutate()
                .header(TRACE_ID_HEADER, traceId)
                .build();

        exchange.getAttributes().put("traceId", traceId);
        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

        log.debug("ENTROU NO TRACING FILTER - traceId={}", traceId);

        // Coloca no contexto reativo
        return chain.filter(mutatedExchange)
                .contextWrite(ctx -> ctx.put(TRACE_ID_HEADER, traceId));
    }

}
