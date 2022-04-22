package ru.test.sms.market;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.test.sms.market.order.CreateOrderReq;
import ru.test.sms.market.order.LimitOrder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/gateway")
public class TradingGatewayController {

    private final TradingGatewaySimulator tradingGatewaySimulator;

    public TradingGatewayController(TradingGatewaySimulator tradingGateway) {
        this.tradingGatewaySimulator = tradingGateway;
    }

    @PostMapping("/{stockName}")
    public ResponseEntity<URI> addOrder(
            @RequestHeader("accountId") UUID accountId,
            @PathVariable String stockName,
            @RequestBody CreateOrderReq order
    ) {
        final LimitOrder createdOrder = tradingGatewaySimulator.addOrder(accountId, stockName, order);
        return ResponseEntity.created(URI.create(createdOrder.getOrderId().toString())).build();
    }

    @DeleteMapping("/{stockName}/{orderId}")
    public void cancelOrder(
            @RequestHeader("accountId") UUID accountId,
            @PathVariable String stockName,
            @PathVariable UUID orderId
    ) {
        tradingGatewaySimulator.cancelOrder(accountId, stockName, orderId);
    }
}
