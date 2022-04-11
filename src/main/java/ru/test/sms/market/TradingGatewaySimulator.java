package ru.test.sms.market;

import org.springframework.stereotype.Service;
import ru.test.sms.market.order.CancelOrderReq;
import ru.test.sms.market.order.CreateOrderReq;
import ru.test.sms.market.order.LimitOrder;

import java.time.Instant;
import java.util.UUID;

@Service
public class TradingGatewaySimulator {

    private final MatchingEngine matchingEngine;

    public TradingGatewaySimulator(MatchingEngine matchingEngine) {
        this.matchingEngine = matchingEngine;
    }

    public LimitOrder addOrder(UUID accountId, String stockName, CreateOrderReq order) {
        final LimitOrder limitOrder = LimitOrder.builder()
                .orderId(UUID.randomUUID())
                .stock(stockName)
                .orderType(order.getOrderType())
                .count(order.getCount())
                .account(accountId)
                .price(order.getPrice())
                .date(Instant.now().toEpochMilli())
                .build();

        matchingEngine.addOrder(limitOrder);
        return limitOrder;
    }

    public void cancelOrder(UUID accountId, String stockName, CancelOrderReq order) {
        matchingEngine.cancelOrder(accountId, stockName, order);
    }
}
