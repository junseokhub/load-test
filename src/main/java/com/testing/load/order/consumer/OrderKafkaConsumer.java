package com.testing.load.order.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testing.load.order.dto.OrderMessage;
import com.testing.load.order.dto.OrderResult;
import com.testing.load.order.service.DefaultOrderService;
import com.testing.load.product.repository.ProductRepository;
import com.testing.load.common.exception.BusinessException;
import com.testing.load.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaConsumer {

    private final ProductRepository productRepository;
    private final DefaultOrderService defaultOrderService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "order-requests", groupId = "order-consumer-group")
    public void consume(String message) {
        try {
            OrderMessage orderMessage = objectMapper.readValue(message, OrderMessage.class);
            processOrder(orderMessage);
        } catch (Exception e) {
            log.error("order-requests 처리 실패: {}", e.getMessage());
        }
    }

    private void processOrder(OrderMessage orderMessage) {
        productRepository.findById(orderMessage.productId())
                .switchIfEmpty(reactor.core.publisher.Mono.error(
                        new BusinessException(ErrorCode.PRODUCT_NOT_FOUND)))
                .flatMap(product -> {
                    if (product.getStock() <= 0) {
                        return reactor.core.publisher.Mono.error(
                                new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK));
                    }
                    product.decrementStock();
                    return productRepository.save(product);
                })
                .flatMap(product -> defaultOrderService.saveOrder(
                        orderMessage.userId(), product, orderMessage.couponIssueId()))
                .doOnSuccess(order -> sendResult(OrderResult.success(
                        orderMessage.correlationId(), order)))
                .doOnError(e -> sendResult(OrderResult.failure(
                        orderMessage.correlationId(), e.getMessage())))
                .subscribe();
    }

    private void sendResult(OrderResult result) {
        try {
            kafkaTemplate.send("order-results", result.correlationId(),
                    objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            log.error("order-results 발행 실패: {}", e.getMessage());
        }
    }
}