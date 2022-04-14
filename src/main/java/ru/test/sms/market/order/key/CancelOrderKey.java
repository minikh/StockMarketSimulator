package ru.test.sms.market.order.key;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CancelOrderKey extends OrderKey {

    private final SellOrderKey sellOrderKey;
    private final BuyOrderKey buyOrderKey;

    @Override
    public int compareTo(OrderKey o) {
        return 0;
    }
}
