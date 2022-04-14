package ru.test.sms.market.trade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.test.sms.market.account.Account;
import ru.test.sms.market.account.AccountService;
import ru.test.sms.market.order.LimitOrder;
import ru.test.sms.market.order.OrderType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
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
                final LimitOrder sellOrder = order.getSellOrder();
                final Optional<Account> sellerAccount = computeTransaction(sellOrder, order.getSum());

                final LimitOrder buyOrder = order.getBuyOrder();
                final Optional<Account> buyerAccount = computeTransaction(buyOrder, order.getSum());

                final Account brokerAccount = accountService.getBrokerAccount();

                sellerAccount.ifPresent(a -> a.commitTransaction(order.getSum()));
                buyerAccount.ifPresent(a -> a.addMoney(order.getSum()));
                brokerAccount.addMoney(2 * accountService.brokerCommission);
            });

            trades.addAll(tradeList);
        } finally {
            lock.unlock();
        }
    }

    private Optional<Account> computeTransaction(LimitOrder order, Integer sum) {
        if (order != null) {
            final Account account = accountService.getAccount(order.getAccount());
            if (order.getOrderType() == OrderType.CANCEL) {
                account.rollbackTransaction(sum);
            } else {
                account.commitTransaction(sum);
            }
            return Optional.of(account);
        }

        return Optional.empty();
    }

    public List<Trade> getTrades() {
        return new ArrayList<>(trades);
    }
}
