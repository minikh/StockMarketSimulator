package ru.test.sms.market;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.test.sms.market.account.Account;
import ru.test.sms.market.account.AccountService;
import ru.test.sms.market.order.LimitOrder;
import ru.test.sms.market.order.OrderBook;
import ru.test.sms.market.trade.Trade;
import ru.test.sms.market.trade.TradeLedger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Slf4j
public class MatchingEngine {
    private final AccountService accountService;
    private final ConcurrentLinkedQueue<LimitOrder> queue = new ConcurrentLinkedQueue<>();

    private final Map<String, OrderBook> orderBooks = new HashMap<>();
    private final TradeLedger tradeLedger;

    public MatchingEngine(AccountService accountService, TradeLedger tradeLedger) {
        this.accountService = accountService;
        this.tradeLedger = tradeLedger;
    }

    public void addOrder(LimitOrder limitOrder) {
        final Account account = accountService.getAccount(limitOrder.getAccount());
        switch (limitOrder.getOrderType()) {
            case BUY:
                account.reserveMoney( accountService.brokerCommission);
                break;
            case SELL:
                account.reserveMoney(limitOrder.getCount() * limitOrder.getPrice() + accountService.brokerCommission);
                break;
        }

        queue.add(limitOrder);
    }

    public void cancelOrder(String stockName, UUID orderId) {
        final OrderBook orderBook = orderBooks.get(stockName);
        if (orderBook == null) throw new IllegalArgumentException("Stock " + stockName + " does not exist");

        orderBook.cancelOrder(orderId);
    }

//    @Scheduled(fixedDelay = 1_000)
    @Scheduled(fixedDelay = 10)
    private void compute() {
        final LimitOrder limitOrder = queue.poll();
        if (limitOrder == null) return;

        final OrderBook orderBook = orderBooks.computeIfAbsent(limitOrder.getStock(), OrderBook::new);
        final List<Trade> tradeList = orderBook.addOrder(limitOrder);

        tradeLedger.add(tradeList);
    }
}
