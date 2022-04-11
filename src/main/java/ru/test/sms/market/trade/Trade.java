package ru.test.sms.market.trade;

import lombok.Builder;
import lombok.Getter;
import ru.test.sms.market.order.LimitOrder;

@Builder
public class Trade {
    private final Integer price;
    private final Integer count;
    private final LimitOrder sellOrder;
    private final LimitOrder buyOrder;

    public LimitOrder getSellOrder() {
        return sellOrder;
    }

    public LimitOrder getBuyOrder() {
        return buyOrder;
    }

    public Integer getSum() {
        return price * count;
    }
}
