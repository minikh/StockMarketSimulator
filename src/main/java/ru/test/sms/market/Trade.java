package ru.test.sms.market;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Trade {
    private final Integer price;
    private Integer count;

    private LimitOrder sellOrder;
    private LimitOrder buyOrder;
}
