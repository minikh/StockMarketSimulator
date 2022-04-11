package ru.test.sms.market.order;


import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode
public class BuyOrderKey implements Comparable<BuyOrderKey> {

    private final UUID orderId;
    private final Long date;
    private final Integer price;

    public BuyOrderKey(UUID orderId, Long date, Integer price) {
        this.orderId = orderId;
        this.date = date;
        this.price = price;
    }

    @Override
    public int compareTo(BuyOrderKey o) {
        if (price > o.price) return -1;
        if (price < o.price) return 1;

        return date.compareTo(o.date);
    }
}
