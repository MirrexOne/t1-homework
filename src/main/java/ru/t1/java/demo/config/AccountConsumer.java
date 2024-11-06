package ru.t1.java.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.service.AccountService;

@Component
@RequiredArgsConstructor
public class AccountConsumer {

    private final AccountService accountService;

    @KafkaListener(topics = "t1_demo_accounts", groupId = "account-group")
    public void consumeAccount(Account account) {
        accountService.createAccount(account);
    }
}