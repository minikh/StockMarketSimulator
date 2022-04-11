package ru.test.sms.market;

import lombok.Getter;
import ru.test.sms.market.LimitOrder;

@Getter
public class Trade {
    private LimitOrder sellOrder;
    private LimitOrder buyOrder;
}
