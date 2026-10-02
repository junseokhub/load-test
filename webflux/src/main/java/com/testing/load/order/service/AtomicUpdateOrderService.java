package com.testing.load.order.service;

import com.testing.load.common.exception.BusinessException;
import com.testing.load.common.exception.ErrorCode;
import com.testing.load.order.domain.Order;
import com.testing.load.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AtomicUpdateOrderService implements OrderService {

    private final ProductRepository productRepository;
    private final DatabaseClient databaseClient;
    private final DefaultOrderService defaultOrderService;

    @Override
    @Transactional
    public Mono<Order> createOrder(Long userId, Long productId, Long couponIssueId, String correlationId) {
        return databaseClient
                .sql("UPDATE products SET stock = stock - 1 WHERE id = :id AND stock > 0")
                .bind("id", productId)
                .fetch()
                .rowsUpdated()
                .flatMap(updatedRows -> {
                    if (updatedRows == 0) {
                        return Mono.error(new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK));
                    }
                    return productRepository.findById(productId);
                })
                .flatMap(product -> defaultOrderService.saveOrder(userId, product, couponIssueId, correlationId));
    }
}