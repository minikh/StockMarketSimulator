package ru.test.sms.market;


import lombok.EqualsAndHashCode;

import java.util.UUID;

@EqualsAndHashCode
public class SellOrderKey implements Comparable<SellOrderKey> {

    private final UUID orderId;
    private final Long date;
    private final Integer price;

    public SellOrderKey(UUID orderId, Long date, Integer price) {
        this.orderId = orderId;
        this.date = date;
        this.price = price;
    }

    @Override
    public int compareTo(SellOrderKey o) {
        if (price < o.price) return -1;
        if (price > o.price) return 1;

        return date.compareTo(o.date);
    }
}
