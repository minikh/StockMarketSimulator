package ru.test.sms.market.order;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CancelOrderReq {
    private final OrderType orderType;
    private final Long date;
    private final Integer price;
}
