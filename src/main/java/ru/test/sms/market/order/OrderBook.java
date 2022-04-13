package ru.test.sms.market.order;

import lombok.extern.slf4j.Slf4j;
import ru.test.sms.config.MarketWebSocket;
import ru.test.sms.market.trade.Trade;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

@Slf4j
public class OrderBook {
    final String symbol;
    private final MarketWebSocket marketWebSocket;
    private final ConcurrentSkipListMap<BuyOrderKey, LimitOrder> buyOrders = new ConcurrentSkipListMap<>();
    private final ConcurrentSkipListMap<SellOrderKey, LimitOrder> sellOrders = new ConcurrentSkipListMap<>();

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
                tradeList = buy(limitOrder);
                break;
            case SELL:
                tradeList = sell(limitOrder);
                break;
        }

        return tradeList != null ? tradeList : new ArrayList<>();
    }

    private List<Trade> buy(LimitOrder buyOrder) {
        Integer needCount = buyOrder.getCount();
        final List<Trade> tradeList = new ArrayList<>();

        do {
            final Map.Entry<SellOrderKey, LimitOrder> sellOrder = sellOrders.lastEntry();
            if (sellOrder != null && sellOrder.getValue().getPrice() >= buyOrder.getPrice()) {
                final Integer sellCount = sellOrder.getValue().getCount();
                final String message;
                if (sellCount <= needCount) {
                    sellOrders.remove(sellOrder.getKey());
                    message = String.format(sellMessage, sellCount, sellOrder.getValue().getAccount(), sellOrder.getValue().getPrice(), buyOrder.getPrice());
                    needCount -= sellCount;
                    buyOrder.minusCount(sellCount);
                } else {
                    sellOrder.getValue().minusCount(needCount);
                    message = String.format(sellMessage, needCount, sellOrder.getValue().getAccount(), sellOrder.getValue().getPrice(), buyOrder.getPrice());
                    needCount = 0;
                }
                log.info(message);
                marketWebSocket.sendMessage(message);

                tradeList.add(Trade.builder()
                        .buyOrder(buyOrder).sellOrder(sellOrder.getValue())
                        .count(sellCount)
                        .price(sellOrder.getValue().getPrice())
                        .build());
            } else {
                buyOrders.put(buyOrder.createBuyKey(), buyOrder);
                marketWebSocket.sendMessage(buyOrder.info());
                break;
            }
        } while (needCount != 0);

        log.info("Заявок на продажу: {}. Заявок на покупку: {}", sellOrders.size(), buyOrders.size());

        return tradeList;
    }

    private List<Trade> sell(LimitOrder sellOrder) {
        Integer needCount = sellOrder.getCount();
        final List<Trade> tradeList = new ArrayList<>();

        do {
            final Map.Entry<BuyOrderKey, LimitOrder> buyOrder = buyOrders.lastEntry();
            if (buyOrder != null && buyOrder.getValue().getPrice() <= sellOrder.getPrice()) {
                final Integer buyCount = buyOrder.getValue().getCount();
                final String message;
                if (buyCount <= needCount) {
                    buyOrders.remove(buyOrder.getKey());
                    message = String.format(buyMessage, buyCount, buyOrder.getValue().getAccount(), buyOrder.getValue().getPrice(), sellOrder.getPrice());
                    needCount -= buyCount;
                    sellOrder.minusCount(buyCount);
                } else {
                    buyOrder.getValue().minusCount(needCount);
                    message = String.format(sellMessage, needCount, buyOrder.getValue().getAccount(), buyOrder.getValue().getPrice(), sellOrder.getPrice());
                    needCount = 0;
                }
                log.info(message);
                marketWebSocket.sendMessage(message);

                tradeList.add(Trade.builder()
                        .buyOrder(buyOrder.getValue()).sellOrder(sellOrder)
                        .count(buyCount)
                        .price(buyOrder.getValue().getPrice())
                        .build());
            } else {
                sellOrders.put(sellOrder.createSellKey(), sellOrder);
                marketWebSocket.sendMessage(sellOrder.info());
                break;
            }
        } while (needCount != 0);

        log.info("Заявок на продажу: {}. Заявок на покупку: {}", sellOrders.size(), buyOrders.size());

        return tradeList;
    }

    public void cancelOrder(UUID orderId) {

    }
}
