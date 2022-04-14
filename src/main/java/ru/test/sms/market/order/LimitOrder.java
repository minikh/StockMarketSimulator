package ru.test.sms.market.order;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.test.sms.market.order.key.BuyOrderKey;
import ru.test.sms.market.order.key.CancelOrderKey;
import ru.test.sms.market.order.key.OrderKey;
import ru.test.sms.market.order.key.SellOrderKey;

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

    public OrderKey createKey() {
        final OrderKey key;
        switch (orderType) {
            case BUY:
                key = new BuyOrderKey(orderId, date, price);
                break;
            case SELL:
                key = new SellOrderKey(orderId, date, price);
                break;
            case CANCEL:
                key = new CancelOrderKey(new SellOrderKey(orderId, date, price), new BuyOrderKey(orderId, date, price));
                break;
            default:
                throw new RuntimeException();
        }
        return key;
    }

    public void minusCount(Integer count) {
        if (this.count < count) throw new IllegalArgumentException("Количество не может быть отрицатьельным");

        this.count -= count;
    }

    public String info() {
        return String.format("Order with ID %s added: %s %s %s @ %s", orderId, orderType, stock, count, price);
    }
}
