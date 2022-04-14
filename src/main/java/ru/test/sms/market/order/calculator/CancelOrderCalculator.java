package ru.test.sms.market.order.calculator;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import ru.test.sms.market.MarketWebSocket;
import ru.test.sms.market.order.LimitOrder;
import ru.test.sms.market.order.key.OrderKey;
import ru.test.sms.market.trade.Trade;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

@Builder
@Slf4j
public class CancelOrderCalculator implements Calculator {

    private final LimitOrder cancelOrder;
    private final ConcurrentSkipListMap<OrderKey, LimitOrder> buyOrders;
    private final ConcurrentSkipListMap<OrderKey, LimitOrder> sellOrders;
    private final String formatMessage;
    private final MarketWebSocket marketWebSocket;

    public List<Trade> computeOrder() {
        final List<Trade> tradeList = new ArrayList<>();

        final OrderKey key = cancelOrder.createKey();

        LimitOrder buyOrder = buyOrders.get(key);
        if (buyOrder != null) {
            buyOrders.remove(key);
            marketWebSocket.sendMessage(String.format(formatMessage, buyOrder.getOrderId(), buyOrder.getStock()));

            tradeList.add(Trade.builder()
                    .buyOrder(buyOrder)
                    .count(buyOrder.getCount())
                    .price(buyOrder.getPrice())
                    .build());
        } else {
            LimitOrder sellOrder = sellOrders.get(key);
            if (sellOrder != null) {
                sellOrders.remove(key);
                marketWebSocket.sendMessage(String.format(formatMessage, sellOrder.getOrderId(), sellOrder.getStock()));

                tradeList.add(Trade.builder()
                        .sellOrder(sellOrder)
                        .count(sellOrder.getCount())
                        .price(sellOrder.getPrice())
                        .build());
            }
        }

        return tradeList;
    }
}
