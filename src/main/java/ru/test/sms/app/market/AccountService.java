package ru.test.sms.app.market;

import org.springframework.stereotype.Service;
import ru.test.sms.app.Account;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AccountService {

    private final Map<UUID, Account> accounts = new HashMap<>();

    public Account getAccount(UUID accountId) {
        return accounts.computeIfAbsent(accountId, k -> Account.builder().accountId(k).balance(10_000L).build());
    }

    private void makeTransaction() {

    }
}
