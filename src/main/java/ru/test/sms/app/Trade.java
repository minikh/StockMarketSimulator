package ru.test.sms.app;

import lombok.Getter;

@Getter
public class Trade {
    private LimitOrder sellOrder;
    private LimitOrder buyOrder;
}
