package ru.t1.java.demo.util;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.Account;
import ru.t1.java.demo.model.Transaction;
import ru.t1.java.demo.model.emuns.AccountType;
import ru.t1.java.demo.repository.AccountRepository;
import ru.t1.java.demo.repository.TransactionRepository;
import net.datafaker.Faker;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class DataGenerator {

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    private final Faker faker = new Faker();

    @PostConstruct
    public void generateData() {
        generateAccounts();
        generateTransactions();
    }

    private void generateAccounts() {
        List<Account> accounts = IntStream.range(0, 100)
                .mapToObj(i -> generateAccount())
                .collect(Collectors.toList());
        accountRepository.saveAll(accounts);
    }

    private Account generateAccount() {
        Account account = new Account();
        account.setClientId(faker.number().randomNumber());
        account.setAccountType(faker.options().option(AccountType.class));
        account.setBalance(faker.number().randomDouble(2, 0, 100000));
        return account;
    }

    private void generateTransactions() {
        List<Transaction> transactions = accountRepository.findAll().stream()
                .flatMap(account -> IntStream.range(0, faker.number().numberBetween(10, 50))
                        .mapToObj(i -> generateTransaction(account)))
                .collect(Collectors.toList());
        transactionRepository.saveAll(transactions);
    }

    private Transaction generateTransaction(Account account) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(account.getId());
        transaction.setAmount(faker.number().randomDouble(2, 0, 10000));
        transaction.setTimestamp(LocalDateTime.now().minusDays(faker.number().numberBetween(0, 365)));
        return transaction;
    }
}
