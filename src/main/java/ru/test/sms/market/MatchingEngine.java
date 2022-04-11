package ru.test.sms.market;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class MatchingEngine {
    private final ConcurrentLinkedQueue<LimitOrder> queue = new ConcurrentLinkedQueue<>();

    private final Map<String, OrderBook> orderBooks = new HashMap<>();


    public void addOrder(LimitOrder limitOrder) {
        queue.add(limitOrder);
    }

    @Scheduled(fixedDelay = 1_000)
    private void compute() {
        LimitOrder limitOrder = queue.poll();
        if (limitOrder == null) return;

        final OrderBook orderBook = orderBooks.computeIfAbsent(limitOrder.getStock(), OrderBook::new);
        orderBook.addOrder(limitOrder);
    }

    public void cancelOrder(String stockName, UUID orderId) {
        final OrderBook orderBook = orderBooks.get(stockName);
        if (orderBook == null) throw new IllegalArgumentException("Stock " + stockName + " does not exist");

        orderBook.cancelOrder(orderId);
    }
}
