package com.pocdatabase.demo.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGateway {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
//                .route("siricascudo",
//                        r -> r.path("/products")
//                                .filters( f -> f.rewritePath( "/products", "/api/products"))
//                        .uri("http://localhost:8081"))
                .route("json_placehoder", r -> r.path("/users")
                        .uri("https://jsonplaceholder.typicode.com"))
                .build();
    }
}
