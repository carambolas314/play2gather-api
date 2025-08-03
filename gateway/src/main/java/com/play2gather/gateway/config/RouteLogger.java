package com.play2gather.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
public class RouteLogger {

    private final RouteLocator routeLocator;

    public RouteLogger(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    @PostConstruct
    public void logRoutes() {
        routeLocator.getRoutes().subscribe(route -> {
            System.out.println("📌 ROUTE " + route.getId() + " -> " + route.getUri());
            System.out.println("   Predicates: " + route.getPredicate());
        });
    }
}
