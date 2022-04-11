package ru.test.sms.market.order;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Builder
@ToString
public class LimitOrder {
    private final UUID orderId;
    private final OrderType orderType;
    private final String stock;
    private final UUID account;
    private final Integer price;
    private final Long date;

    private Integer count;

    public SellOrderKey createSellKey() {
        return new SellOrderKey(orderId, date, price);
    }

    public BuyOrderKey createBuyKey() {
        return new BuyOrderKey(orderId, date, price);
    }

    public void minusCount(Integer count) {
        if (this.count < count) throw new IllegalArgumentException("Количество не может быть отрицатьельным");

        this.count -= count;
    }
}
