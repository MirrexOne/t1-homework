package ru.t1.java.demo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.service.AccountService;
import org.springframework.kafka.support.KafkaHeaders;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountConsumer {

    private final AccountService accountService;

    @KafkaListener(topics = "t1_demo_accounts", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeAccount(
            @Payload Account account,
            @Header(KafkaHeaders.RECEIVED_KEY) String key) {
        String messageKey = key != null ? key : UUID.randomUUID().toString();
        log.info("Received account message with key: {}", messageKey);
        accountService.createAccount(account);
    }
}
