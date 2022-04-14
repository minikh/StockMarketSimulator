package ru.test.sms.market;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.test.sms.market.order.CreateOrderReq;
import ru.test.sms.market.order.LimitOrder;
import ru.test.sms.market.order.OrderType;

import java.util.UUID;

@ShellComponent
public class MarketTerminal {

    private final TradingGatewaySimulator tradingGatewaySimulator;
    private final UUID defaultAccountId = UUID.fromString("b4627c3e-df4d-4398-a788-f9a5ab15b2bd");

    public MarketTerminal(TradingGatewaySimulator tradingGatewaySimulator) {
        this.tradingGatewaySimulator = tradingGatewaySimulator;
    }

    @ShellMethod("Add new order")
    public String add(String stockName, OrderType orderType, Integer count, Integer price) {

        final CreateOrderReq orderReq = CreateOrderReq.builder().orderType(orderType).count(count).price(price).build();
        final LimitOrder order = tradingGatewaySimulator.addOrder(defaultAccountId, stockName, orderReq);

        return order.info();
    }

    @ShellMethod("Cancel order")
    public String cancel(String stockName, UUID orderId) {

        tradingGatewaySimulator.cancelOrder(defaultAccountId, stockName, orderId);

        return "Order canceled";
    }
}
