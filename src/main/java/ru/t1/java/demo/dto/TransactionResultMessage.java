package ru.t1.java.demo.dto;

import lombok.Value;
import ru.t1.java.demo.model.emuns.TransactionStatus;

@Value
public class TransactionResultMessage {
    String transactionId;
    String accountId;
    TransactionStatus status;
}
