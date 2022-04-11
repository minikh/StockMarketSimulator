package ru.test.sms.market.order;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateOrderReq {
    private final OrderType orderType;
    private final Integer count;
    private final Integer price;
}
