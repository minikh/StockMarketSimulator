package ru.test.sms.app.market;

import ru.test.sms.app.LimitOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OrderBook {
    final String symbol;
    private final List<LimitOrder> orders = new ArrayList<>();

    public OrderBook(String symbol) {
        this.symbol = symbol;
    }

    public void addOrder(LimitOrder limitOrder) {

    }

    public void cancelOrder(UUID orderId) {

    }
}
