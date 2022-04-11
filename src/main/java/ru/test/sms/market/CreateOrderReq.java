package ru.test.sms.market;

import lombok.Data;

@Data
public class CreateOrderReq {
    private final OrderType orderType;
    private final Integer count;
}
