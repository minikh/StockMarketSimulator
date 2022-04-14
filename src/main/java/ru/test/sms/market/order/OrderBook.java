package ru.test.sms.market.order;

import lombok.extern.slf4j.Slf4j;
import ru.test.sms.config.MarketWebSocket;
import ru.test.sms.market.order.key.OrderKey;
import ru.test.sms.market.trade.Trade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

@Slf4j
public class OrderBook {
    final String symbol;
    private final MarketWebSocket marketWebSocket;
    private final ConcurrentSkipListMap<OrderKey, LimitOrder> buyOrders = new ConcurrentSkipListMap<>();
    private final ConcurrentSkipListMap<OrderKey, LimitOrder> sellOrders = new ConcurrentSkipListMap<>();

    private final static String sellMessage = "Продано: %s шт %s по цене %s (%s)";
    private final static String buyMessage = "Куплено: %s шт у %s по цене %s (%s)";

    public OrderBook(String symbol, MarketWebSocket marketWebSocket) {
        this.symbol = symbol;
        this.marketWebSocket = marketWebSocket;
    }

    public List<Trade> addOrder(LimitOrder limitOrder) {
        List<Trade> tradeList = null;
        switch (limitOrder.getOrderType()) {
            case BUY:
                tradeList = computeOrder(limitOrder, buyOrders, sellOrders, sellMessage);
                break;
            case SELL:
                tradeList = computeOrder(limitOrder, sellOrders, buyOrders, buyMessage);
                break;
        }

        log.info("Заявок на продажу: {}. Заявок на покупку: {}", sellOrders.size(), buyOrders.size());

        return tradeList != null ? tradeList : new ArrayList<>();
    }

    private boolean hasOppositeOrder(Map.Entry<OrderKey, LimitOrder> orderFromDom, LimitOrder newOrder) {
        boolean result = false;
        switch (newOrder.getOrderType()) {
            case BUY:
                result = orderFromDom != null && orderFromDom.getValue().getPrice() >= newOrder.getPrice();
                break;
            case SELL:
                result = orderFromDom != null && orderFromDom.getValue().getPrice() <= newOrder.getPrice();
                break;
        }
        return result;
    }

    private List<Trade> computeOrder(
            LimitOrder newOrder,
            ConcurrentSkipListMap<OrderKey, LimitOrder> orders,
            ConcurrentSkipListMap<OrderKey, LimitOrder> oppositeOrders,
            String formatMessage
    ) {
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

    public void cancelOrder(UUID orderId) {

    }
}
