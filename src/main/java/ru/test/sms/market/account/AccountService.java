package ru.test.sms.market.account;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AccountService {

    public final Long startBalance;
    public final Integer brokerCommission;

    private final Map<UUID, Account> accounts = new ConcurrentHashMap<>();

    public AccountService(
            @Value("${startBalance}") Long startBalance,
            @Value("${broker.commission}") Integer brokerCommission
    ) {
        this.startBalance = startBalance;
        this.brokerCommission = brokerCommission;
    }

    public Account getAccount(UUID accountId) {
        return accounts.computeIfAbsent(accountId, k -> Account.builder().accountId(k).balance(startBalance).build());
    }

    public Account getBrokerAccount() {
        return getAccount(UUID.fromString("307361a5-725b-4f42-9f57-10a93add9083"));
    }
}
