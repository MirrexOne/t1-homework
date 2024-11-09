package ru.t1.java.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.t1.java.demo.dto.TransactionMessage;
import ru.t1.java.demo.dto.TransactionResultMessage;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.emuns.TransactionStatus;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class Service2 {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, TransactionResultMessage> kafkaTemplate;

    @Value("${transaction.limit.count}")
    private int transactionLimit;

    @Value("${transaction.limit.time-window-minutes}")
    private int timeWindowMinutes;

    @KafkaListener(topics = "t1_demo_transaction_accept")
    public void processTransaction(TransactionMessage message) {
        LocalDateTime windowStart = message.getTimestamp().minusMinutes(timeWindowMinutes);

        List<Transaction> recentTransactions = transactionRepository
                .findByAccountIdAndTimestampBetween(
                        message.getAccountId(),
                        windowStart,
                        message.getTimestamp()
                );

        Account account = accountRepository.findByAccountId(message.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        TransactionResultMessage resultMessage = getTransactionResultMessage(message, recentTransactions, account);

        kafkaTemplate.send("t1_demo_transaction_result", resultMessage);
    }

    private TransactionResultMessage getTransactionResultMessage(
            TransactionMessage message, List<Transaction> recentTransactions, Account account) {
        TransactionResultMessage resultMessage;

        if (recentTransactions.size() >= transactionLimit) {
            resultMessage = new TransactionResultMessage(
                    message.getTransactionId(),
                    message.getAccountId(),
                    TransactionStatus.BLOCKED
            );
        } else if (message.getAmount() > account.getBalance()) {
            resultMessage = new TransactionResultMessage(
                    message.getTransactionId(),
                    message.getAccountId(),
                    TransactionStatus.REJECTED
            );
        } else {
            resultMessage = new TransactionResultMessage(
                    message.getTransactionId(),
                    message.getAccountId(),
                    TransactionStatus.ACCEPTED
            );
        }
        return resultMessage;
    }
}
