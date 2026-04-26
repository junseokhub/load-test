package com.testing.load.order.service;

import com.testing.load.order.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class KafkaOrderService implements OrderService {

    private final DefaultOrderService defaultOrderService;

    @Override
    public Mono<Order> createOrder(Long userId, Long productId, Long couponIssueId) {
        // TODO: Kafka 구현 예정
        return Mono.error(new UnsupportedOperationException("Kafka 구현 예정"));
    }
}