package ru.test.sms.app;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class LimitOrder {
    private final UUID orderId;
    private final OrderType orderType;
    private final String stock;
    private final Account account;
    private final Integer count;
}
