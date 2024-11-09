package ru.t1.java.demo.dto;

import lombok.Value;
import java.time.LocalDateTime;

@Value
public class TransactionMessage {
    Long clientId;
    String accountId;
    String transactionId;
    LocalDateTime timestamp;
    Double amount;
    Double balance;
}
