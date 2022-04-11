package ru.test.sms.market.trade;

import org.springframework.stereotype.Service;
import ru.test.sms.market.account.Account;
import ru.test.sms.market.account.AccountService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TradeLedger {

    private final AccountService accountService;
    private final List<Trade> trades = new ArrayList<>();
    private final Lock lock = new ReentrantLock();

    public TradeLedger(AccountService accountService) {
        this.accountService = accountService;
    }

    public void add(List<Trade> tradeList) {
        lock.lock();
        try {
            trades.forEach(order -> {
                final Account sellerAccount = accountService.getAccount(order.getSellOrder().getAccount());
                final Account buyerAccount = accountService.getAccount(order.getBuyOrder().getAccount());
                final Account brokerAccount = accountService.getBrokerAccount();

                sellerAccount.commitTransaction(order.getSum());
                buyerAccount.addMoney(order.getSum());
                brokerAccount.addMoney(2 * accountService.brokerCommission);
            });

            trades.addAll(tradeList);
        } finally {
            lock.unlock();
        }

    }

    public List<Trade> getTrades() {
        return new ArrayList<>(trades);
    }
}
