package ru.test.sms.market;


import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode
public class LimitOrderKey implements Comparable<LimitOrderKey> {

    private final UUID orderId;
    private final Long date;
    private final Integer price;

    public LimitOrderKey(UUID orderId, Long date, Integer price) {
        this.orderId = orderId;
        this.date = date;
        this.price = price;
    }

    @Override
    public int compareTo(LimitOrderKey o) {
        if (price < o.price) return -1;
        if (price > o.price) return 1;

        if (date < o.date) return -1;
        if (date > o.date) return -1;

        return 0;
    }
}
