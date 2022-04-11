package ru.test.sms.market.trade;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TradeLedger {
    private final List<Trade> trades = new ArrayList<>();

    public void add(List<Trade> tradeList) {
        trades.addAll(tradeList);
    }

    public List<Trade> getTrades() {
        return new ArrayList<>(trades);
    }
}
