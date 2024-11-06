package ru.t1.java.demo.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.TransactionService;
import org.springframework.kafka.support.KafkaHeaders;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionConsumer {

    private final TransactionService transactionService;

    @KafkaListener(topics = "t1_demo_transactions", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeTransaction(
            @Payload Transaction transaction,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        String messageKey = key != null ? key : UUID.randomUUID().toString();
        log.info("Received transaction message with key: {}", messageKey);
        transactionService.createTransaction(transaction);
    }
}