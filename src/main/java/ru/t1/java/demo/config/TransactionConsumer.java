package ru.t1.java.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.service.TransactionService;

@Component
@RequiredArgsConstructor
public class TransactionConsumer {

    private final TransactionService transactionService;

    @KafkaListener(topics = "t1_demo_transactions", groupId = "transaction-group")
    public void consumeTransaction(Transaction transaction) {
        transactionService.createTransaction(transaction);
    }
}