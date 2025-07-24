package com.play2gather.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Component
@Order(-1)
@Slf4j
public class LoggingFilter implements GlobalFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.debug("ENTROU NO LOG FILTER");

        String requestId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();
        exchange.getAttributes().put("requestId", requestId);

        return Mono.deferContextual(ctx -> {
            String traceId = ctx.getOrDefault(TRACE_ID_HEADER, "undefined");

            // Cria uma cópia manual do contexto
            return chain.filter(exchange).doOnSuccess(done -> {
                try {
                    MDC.put("traceId", traceId); // <- Reinsere no contexto da thread atual

                    long duration = System.currentTimeMillis() - startTime;
                    String logJson = "\n{\n" +
                            "  \"timestamp\": \"" + Instant.now() + "\",\n" +
                            "  \"traceId\": \"" + traceId + "\",\n" +
                            "  \"requestId\": \"" + requestId + "\",\n" +
                            "  \"method\": \"" + exchange.getRequest().getMethod() + "\",\n" +
                            "  \"path\": \"" + exchange.getRequest().getURI().getPath() + "\",\n" +
                            "  \"status\": " + (exchange.getResponse().getStatusCode() != null ? exchange.getResponse().getStatusCode().value() : "null") + ",\n" +
                            "  \"durationMs\": " + duration + "\n" +
                            "}";

                    log.info(logJson);
                } finally {
                    MDC.remove("traceId");
                }
            });
        });
    }
}
