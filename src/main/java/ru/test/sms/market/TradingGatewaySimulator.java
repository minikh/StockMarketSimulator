package ru.test.sms.market;

import org.springframework.stereotype.Service;
import ru.test.sms.market.account.Account;
import ru.test.sms.market.account.AccountService;
import ru.test.sms.market.order.CreateOrderReq;
import ru.test.sms.market.order.LimitOrder;

import java.time.Instant;
import java.util.UUID;

@Service
public class TradingGatewaySimulator {

    private final MatchingEngine matchingEngine;
    private final AccountService accountService;

    public TradingGatewaySimulator(
            MatchingEngine matchingEngine,
            AccountService accountService
    ) {
        this.matchingEngine = matchingEngine;
        this.accountService = accountService;
    }

    public LimitOrder addOrder(UUID accountId, String stockName, CreateOrderReq order) {
        final Account account = accountService.getAccount(accountId);

        final LimitOrder limitOrder = LimitOrder.builder()
                .orderId(UUID.randomUUID())
                .stock(stockName)
                .orderType(order.getOrderType())
                .count(order.getCount())
                .account(account)
                .price(order.getPrice())
                .date(Instant.now().toEpochMilli())
                .build();

        matchingEngine.addOrder(limitOrder);
        return limitOrder;
    }

    public void cancelOrder(UUID accountId, String stockName, UUID orderId) {
        final Account account = accountService.getAccount(accountId);

        matchingEngine.cancelOrder(stockName, orderId);



    }
}
