package com.testing.load.order.service.config;

import com.testing.load.common.properties.OrderProperties;
import com.testing.load.order.service.*;
import com.testing.load.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.r2dbc.core.DatabaseClient;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class OrderServiceConfig {

    private final OrderProperties orderProperties;
    private final ProductRepository productRepository;
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final DefaultOrderService defaultOrderService;
    private final DatabaseClient databaseClient;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Bean
    public OrderService orderService() {
        OrderService service = switch (orderProperties.serviceType()) {
            case REDIS_LUA -> new RedisLuaOrderService(
                    productRepository, reactiveRedisTemplate, defaultOrderService);
            case OPTIMISTIC_LOCK -> new OptimisticLockOrderService(
                    productRepository, defaultOrderService);
            case PESSIMISTIC_LOCK -> new PessimisticLockOrderService(
                    productRepository, databaseClient, defaultOrderService);
            case KAFKA_ASYNC -> new KafkaAsyncOrderService(kafkaTemplate);
            case KAFKA_SYNC -> new KafkaSyncOrderService(kafkaTemplate);
        };
        log.info("OrderService 구현체: {}", service.getClass().getSimpleName());
        return service;
    }
}