package com.play2gather.gateway.filter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class LoggingFilterTests {

    @Test
    void testLoggingFilterExecutesWithoutErrors() {
        System.out.println("🔍 Iniciando teste: LoggingFilter cria requestId");

        LoggingFilter filter = new LoggingFilter();

        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilterChain chain = Mockito.mock(GatewayFilterChain.class);
        Mockito.when(chain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(exchange, chain);
        result.block();

        assertTrue(exchange.getAttributes().containsKey("requestId"));

        System.out.println("✅ requestId presente no exchange");
    }
}
