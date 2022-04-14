package ru.test.sms.market.order.calculator;

import ru.test.sms.market.trade.Trade;

import java.util.List;

public interface Calculator {
    public List<Trade> computeOrder();
}
