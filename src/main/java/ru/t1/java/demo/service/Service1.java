package ru.t1.java.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.enums.AccountStatus;
import ru.t1.java.demo.model.enums.TransactionStatus;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;

@Service
@RequiredArgsConstructor
public class Service1 {
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, TransactionMessage> kafkaTemplate;

    @KafkaListener(topics = "t1_demo_transactions")
    @Transactional
    public void processTransaction(TransactionMessage message) {
        Account account = accountRepository.findByAccountId(message.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getStatus() == AccountStatus.OPEN) {
            Transaction transaction = new Transaction();
            transaction.setTransactionId(generateUniqueTransactionId());
            transaction.setAccountId(account.getId());
            transaction.setAmount(message.getAmount());
            transaction.setTimestamp(message.getTimestamp());
            transaction.setStatus(TransactionStatus.REQUESTED);
            
            transactionRepository.save(transaction);
            
            account.setBalance(account.getBalance() + message.getAmount());
            accountRepository.save(account);
            
            TransactionMessage acceptMessage = new TransactionMessage(
                account.getClientId(),
                account.getAccountId(),
                transaction.getTransactionId(),
                transaction.getTimestamp(),
                transaction.getAmount(),
                account.getBalance()
            );
            
            kafkaTemplate.send("t1_demo_transaction_accept", acceptMessage);
        }
    }

    @KafkaListener(topics = "t1_demo_transaction_result")
    @Transactional
    public void processTransactionResult(TransactionResultMessage message) {
        Transaction transaction = transactionRepository.findByTransactionId(message.getTransactionId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        Account account = accountRepository.findByAccountId(message.getAccountId())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        switch (message.getStatus()) {
            case ACCEPTED:
                transaction.setStatus(TransactionStatus.ACCEPTED);
                transactionRepository.save(transaction);
                break;
                
            case BLOCKED:
                transaction.setStatus(TransactionStatus.BLOCKED);
                account.setStatus(AccountStatus.BLOCKED);
                account.setBalance(account.getBalance() - transaction.getAmount());
                account.setFrozenAmount(account.getFrozenAmount() + transaction.getAmount());
                transactionRepository.save(transaction);
                accountRepository.save(account);
                break;
                
            case REJECTED:
                transaction.setStatus(TransactionStatus.REJECTED);
                account.setBalance(account.getBalance() - transaction.getAmount());
                transactionRepository.save(transaction);
                accountRepository.save(account);
                break;
        }
    }
}
