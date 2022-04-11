package ru.test.sms.market.trade;

import lombok.Builder;
import lombok.Getter;
import ru.test.sms.market.order.LimitOrder;

@Getter
@Builder
public class Trade {
    private final Integer price;
    private Integer count;

    private LimitOrder sellOrder;
    private LimitOrder buyOrder;
}
