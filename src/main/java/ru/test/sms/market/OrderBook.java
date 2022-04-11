package ru.test.sms.market;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

public class OrderBook {
    final String symbol;
    //    private final ConcurrentSkipListMap<Integer, LimitOrder> buyOrders = new ConcurrentSkipListMap<>();
    //    private final ConcurrentSkipListMap<Integer, LimitOrder> sellOrders = new ConcurrentSkipListMap<>();
    private final TreeMap<LimitOrderKey, LimitOrder> buyOrders = new TreeMap<>();
    private final TreeMap<LimitOrderKey, LimitOrder> sellOrders = new TreeMap<>();

    public OrderBook(String symbol) {
        this.symbol = symbol;
    }

    public void addOrder(LimitOrder limitOrder) {
        switch (limitOrder.getOrderType()) {
            case BUY:
                buy(limitOrder);
                break;
            case SELL:
                sell(limitOrder);
                break;
        }
    }

    private void buy(LimitOrder buyOrder) {
        Integer count = buyOrder.getCount();

        do {
            final Map.Entry<LimitOrderKey, LimitOrder> sellOrder = sellOrders.firstEntry();
            if (sellOrder.getValue().getPrice() <= buyOrder.getPrice()) {
                final Integer sellCount = sellOrder.getValue().getCount();
                if (sellCount <= count) {
                    sellOrders.remove(sellOrder.getKey());
                    count -= sellCount;
                } else {
                    sellOrder.getValue().minusCount(count);
                    count = 0;
                }
            } else {
                buyOrders.put(buyOrder.createKey(), buyOrder);
                count = 0;
            }
        } while (count != 0);
    }

    private void sell(LimitOrder sellOrder) {
        Integer count = sellOrder.getCount();

        do {
            final Map.Entry<LimitOrderKey, LimitOrder> buyOrder = buyOrders.lastEntry();
            if (buyOrder.getValue().getPrice() <= sellOrder.getPrice()) {
                final Integer buyCount = buyOrder.getValue().getCount();
                if (buyCount <= count) {
                    buyOrders.remove(buyOrder.getKey());
                    count -= buyCount;
                } else {
                    buyOrder.getValue().minusCount(count);
                    count = 0;
                }
            } else {
                sellOrders.put(sellOrder.createKey(), sellOrder);
                count = 0;
            }
        } while (count != 0);
    }

    public void cancelOrder(UUID orderId) {

    }
}
