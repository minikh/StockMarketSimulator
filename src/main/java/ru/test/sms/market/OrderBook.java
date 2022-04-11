package ru.test.sms.market;

import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

@Slf4j
public class OrderBook {
    final String symbol;
    private final ConcurrentSkipListMap<BuyOrderKey, LimitOrder> buyOrders = new ConcurrentSkipListMap<>();
    private final ConcurrentSkipListMap<SellOrderKey, LimitOrder> sellOrders = new ConcurrentSkipListMap<>();
//    private final TreeMap<LimitOrderKey, LimitOrder> buyOrders = new TreeMap<>();
//    private final TreeMap<LimitOrderKey, LimitOrder> sellOrders = new TreeMap<>();

    public OrderBook(String symbol) {
        this.symbol = symbol;
    }

    public List<Trade> addOrder(LimitOrder limitOrder) {
        List<Trade> tradeList = null;
        switch (limitOrder.getOrderType()) {
            case BUY:
//                log.info("Новая заявка на продажу: {}", limitOrder);
                tradeList = buy(limitOrder);
                break;
            case SELL:
//                log.info("Новая заявка на покупку: {}", limitOrder);
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
                if (sellCount <= needCount) {
                    sellOrders.remove(sellOrder.getKey());
                    log.info("Продано: {} шт {} по цене {} ({})", sellCount, sellOrder.getValue().getAccount().getAccountId(), sellOrder.getValue().getPrice(), buyOrder.getPrice());
                    needCount -= sellCount;
                } else {
                    sellOrder.getValue().minusCount(needCount);
                    log.info("Куплено: {} шт {} по цене {} ({})", needCount, sellOrder.getValue().getAccount().getAccountId(), sellOrder.getValue().getPrice(), buyOrder.getPrice());
                    needCount = 0;
                }

                tradeList.add(Trade.builder()
                        .buyOrder(buyOrder).sellOrder(sellOrder.getValue())
                        .count(sellCount)
                        .price(sellOrder.getValue().getPrice())
                        .build());
            } else {
                buyOrders.put(buyOrder.createBuyKey(), buyOrder);
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
                if (buyCount <= needCount) {
                    buyOrders.remove(buyOrder.getKey());
                    log.info("Куплено: {} шт у {} по цене {} ({})", buyCount, buyOrder.getValue().getAccount().getAccountId(), buyOrder.getValue().getPrice(), sellOrder.getPrice());
                    needCount -= buyCount;
                } else {
                    buyOrder.getValue().minusCount(needCount);
                    log.info("Куплено: {} шт у {} по цене {} ({})", needCount, buyOrder.getValue().getAccount().getAccountId(), buyOrder.getValue().getPrice(), sellOrder.getPrice());
                    needCount = 0;
                }

                tradeList.add(Trade.builder()
                        .buyOrder(buyOrder.getValue()).sellOrder(sellOrder)
                        .count(buyCount)
                        .price(buyOrder.getValue().getPrice())
                        .build());
            } else {
                sellOrders.put(sellOrder.createSellKey(), sellOrder);
                break;
            }
        } while (needCount != 0);

        log.info("Заявок на продажу: {}. Заявок на покупку: {}", sellOrders.size(), buyOrders.size());

        return tradeList;
    }

    public void cancelOrder(UUID orderId) {

    }
}
