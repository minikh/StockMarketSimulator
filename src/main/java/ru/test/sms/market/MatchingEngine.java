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
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@Slf4j
public class MatchingEngine {
    private final AccountService accountService;
    private final ConcurrentLinkedQueue<LimitOrder> queue = new ConcurrentLinkedQueue<>();
    private final Map<String, OrderBook> orderBooks = new HashMap<>();
    private final TradeLedger tradeLedger;
    private final MarketWebSocket marketWebSocket;

    public MatchingEngine(AccountService accountService, TradeLedger tradeLedger, MarketWebSocket marketWebSocket) {
        this.accountService = accountService;
        this.tradeLedger = tradeLedger;
        this.marketWebSocket = marketWebSocket;
    }

    public void addOrder(LimitOrder limitOrder) {
        final Account account = accountService.getAccount(limitOrder.getAccount());
        switch (limitOrder.getOrderType()) {
            case BUY:
                account.reserveMoney(accountService.brokerCommission);
                break;
            case SELL:
                account.reserveMoney(limitOrder.getCount() * limitOrder.getPrice() + accountService.brokerCommission);
                break;
        }

        queue.add(limitOrder);
    }

    public void cancelOrder(LimitOrder order) {
        final OrderBook orderBook = orderBooks.get(order.getStock());
        final Account account = accountService.getAccount(order.getAccount());
        if (orderBook == null) throw new IllegalArgumentException("Stock " + order.getStock() + " does not exist");

        queue.add(order);
    }

    //    @Scheduled(fixedDelay = 1_000)
    @Scheduled(fixedDelay = 10)
    private void compute() {
        final LimitOrder limitOrder = queue.poll();
        if (limitOrder == null) return;

        final OrderBook orderBook = orderBooks.computeIfAbsent(limitOrder.getStock(), symbol -> new OrderBook(symbol, marketWebSocket));
        final List<Trade> tradeList = orderBook.addOrder(limitOrder);

        tradeLedger.add(tradeList);
    }
}
