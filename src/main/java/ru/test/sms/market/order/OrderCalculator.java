package ru.test.sms.market.order;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import ru.test.sms.config.MarketWebSocket;
import ru.test.sms.market.order.key.OrderKey;
import ru.test.sms.market.trade.Trade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

@Builder
@Slf4j
public class OrderCalculator {

    private final LimitOrder newOrder;
    private final ConcurrentSkipListMap<OrderKey, LimitOrder> orders;
    private final ConcurrentSkipListMap<OrderKey, LimitOrder> oppositeOrders;
    private final String formatMessage;
    private final MarketWebSocket marketWebSocket;

    public List<Trade> computeOrder() {
        Integer needCount = newOrder.getCount();
        final List<Trade> tradeList = new ArrayList<>();

        do {
            final Map.Entry<OrderKey, LimitOrder> order = oppositeOrders.lastEntry();
            if (hasOppositeOrder(order, newOrder)) {
                final Integer countForCompute = order.getValue().getCount();
                final String message;
                if (countForCompute <= needCount) {
                    oppositeOrders.remove(order.getKey());
                    message = String.format(formatMessage, countForCompute, order.getValue().getAccount(), order.getValue().getPrice(), newOrder.getPrice());
                    needCount -= countForCompute;
                    newOrder.minusCount(countForCompute);
                } else {
                    order.getValue().minusCount(needCount);
                    message = String.format(formatMessage, needCount, order.getValue().getAccount(), order.getValue().getPrice(), newOrder.getPrice());
                    needCount = 0;
                }
                log.info(message);
                marketWebSocket.sendMessage(message);

                tradeList.add(Trade.builder()
                        .buyOrder(order.getValue()).sellOrder(newOrder)
                        .count(countForCompute)
                        .price(order.getValue().getPrice())
                        .build());
            } else {
                orders.put(newOrder.createKey(), newOrder);
                marketWebSocket.sendMessage(newOrder.info());
                break;
            }
        } while (needCount != 0);

        return tradeList;
    }

    private boolean hasOppositeOrder(Map.Entry<OrderKey, LimitOrder> orderFromDom, LimitOrder newOrder) {
        final boolean result;
        switch (newOrder.getOrderType()) {
            case BUY:
                result = orderFromDom != null && orderFromDom.getValue().getPrice() >= newOrder.getPrice();
                break;
            case SELL:
                result = orderFromDom != null && orderFromDom.getValue().getPrice() <= newOrder.getPrice();
                break;
            default:
                throw new RuntimeException();
        }
        return result;
    }
}
