package ru.test.sms.market.order;

import lombok.extern.slf4j.Slf4j;
import ru.test.sms.market.MarketWebSocket;
import ru.test.sms.market.order.calculator.Calculator;
import ru.test.sms.market.order.calculator.CancelOrderCalculator;
import ru.test.sms.market.order.calculator.OrderCalculator;
import ru.test.sms.market.order.key.OrderKey;
import ru.test.sms.market.trade.Trade;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentSkipListMap;

@Slf4j
public class OrderBook {
    final String symbol;
    private final MarketWebSocket marketWebSocket;
    private final ConcurrentSkipListMap<OrderKey, LimitOrder> buyOrders = new ConcurrentSkipListMap<>();
    private final ConcurrentSkipListMap<OrderKey, LimitOrder> sellOrders = new ConcurrentSkipListMap<>();

    private final static String sellMessage = "Продано: %s шт %s по цене %s (%s)";
    private final static String buyMessage = "Куплено: %s шт у %s по цене %s (%s)";
    private final static String cancelMessage = "Отмена заявки: %s %s";

    public OrderBook(String symbol, MarketWebSocket marketWebSocket) {
        this.symbol = symbol;
        this.marketWebSocket = marketWebSocket;
    }

    public List<Trade> addOrder(LimitOrder newOrder) {

        final Calculator orderCalculator;
        switch (newOrder.getOrderType()) {
            case BUY:
                orderCalculator = OrderCalculator.builder()
                        .newOrder(newOrder)
                        .orders(buyOrders)
                        .oppositeOrders(sellOrders)
                        .formatMessage(sellMessage)
                        .marketWebSocket(marketWebSocket)
                        .build();
                break;
            case SELL:
                orderCalculator = OrderCalculator.builder()
                        .newOrder(newOrder)
                        .orders(sellOrders)
                        .oppositeOrders(buyOrders)
                        .formatMessage(buyMessage)
                        .marketWebSocket(marketWebSocket)
                        .build();
                break;
            case CANCEL:
                orderCalculator = CancelOrderCalculator.builder()
                        .cancelOrder(newOrder)
                        .buyOrders(buyOrders)
                        .sellOrders(sellOrders)
                        .formatMessage(cancelMessage)
                        .marketWebSocket(marketWebSocket)
                        .build();
                break;
            default:
                throw new RuntimeException();
        }

        final List<Trade> tradeList = orderCalculator.computeOrder();

        log.info("Заявок на продажу: {}. Заявок на покупку: {}", sellOrders.size(), buyOrders.size());

        return tradeList != null ? tradeList : new ArrayList<>();
    }
}
