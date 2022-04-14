package ru.test.sms.market.order.key;


import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode(callSuper = false)
public class SellOrderKey extends OrderKey {

    private final UUID orderId;
    private final Long date;
    private final Integer price;

    public SellOrderKey(UUID orderId, Long date, Integer price) {
        this.orderId = orderId;
        this.date = date;
        this.price = price;
    }

    @Override
    public int compareTo(OrderKey o) {
        if (o instanceof SellOrderKey) {
            final SellOrderKey other = (SellOrderKey) o;

            if (price < other.price) return -1;
            if (price > other.price) return 1;

            return date.compareTo(other.date);
        } else throw new RuntimeException();
    }
}
