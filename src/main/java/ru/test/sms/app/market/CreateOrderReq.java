package ru.test.sms.app.market;

import lombok.Data;
import ru.test.sms.app.OrderType;

@Data
public class CreateOrderReq {
    private final OrderType orderType;
    private final Integer count;
}
