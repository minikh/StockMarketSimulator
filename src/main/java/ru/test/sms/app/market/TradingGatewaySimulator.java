package ru.test.sms.app.market;

import org.springframework.stereotype.Service;
import ru.test.sms.app.Account;
import ru.test.sms.app.LimitOrder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TradingGatewaySimulator {

    private final MatchingEngine matchingEngine;
    private final AccountService accountService;
    private final Map<String, OrderBook> orderBooks = new HashMap<>();

    public TradingGatewaySimulator(
            MatchingEngine matchingEngine,
            AccountService accountService
    ) {
        this.matchingEngine = matchingEngine;
        this.accountService = accountService;
    }

    public LimitOrder addOrder(UUID accountId, String stockName, CreateOrderReq order) {
        final Account account = accountService.getAccount(accountId);
        final OrderBook orderBook = orderBooks.computeIfAbsent(stockName, OrderBook::new);

        final LimitOrder limitOrder = LimitOrder.builder()
                .orderId(UUID.randomUUID())
                .stock(stockName)
                .orderType(order.getOrderType())
                .count(order.getCount())
                .account(account)
                .build();

        orderBook.addOrder(limitOrder);
        return limitOrder;
    }

    public void cancelOrder(UUID accountId, String stockName, UUID orderId) {
        final OrderBook orderBook = orderBooks.get(stockName);
        if (orderBook == null) throw new IllegalArgumentException("Stock " + stockName + " does not exist");

        orderBook.cancelOrder(orderId);
    }
}
