package com.shopping.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/api/users/**", "/api/auth/**", "/api/addresses/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://user-service"))
                .route("product-service", r -> r.path("/api/products/**", "/api/categories/**", "/api/seckill/**", "/api/product-images/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://product-service"))
                .route("order-service", r -> r.path("/api/orders/**", "/api/cart/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://order-service"))
                .route("payment-service", r -> r.path("/api/payments/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://payment-service"))
                .route("shipment-service", r -> r.path("/api/shipments/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://shipment-service"))
                .route("comment-service", r -> r.path("/api/comments/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://comment-service"))
                .route("stats-service", r -> r.path("/api/stats/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://stats-service"))
                .route("blog-service", r -> r.path("/api/blogs/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://blog-service"))
                .route("sms-service", r -> r.path("/api/sms/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://sms-service"))
                .route("store-service", r -> r.path("/api/stores/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://store-service"))
                .build();
    }
}
