package ru.test.sms.market;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.test.sms.market.order.CancelOrderReq;
import ru.test.sms.market.order.CreateOrderReq;
import ru.test.sms.market.order.LimitOrder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/gateway")
public class TradingGatewayController {

    private final TradingGatewaySimulator matchingEngine;

    public TradingGatewayController(TradingGatewaySimulator tradingGateway) {
        this.matchingEngine = tradingGateway;
    }

    @PostMapping("/{stockName}")
    public ResponseEntity<URI> addOrder(
            @RequestHeader("accountId") UUID accountId,
            @PathVariable String stockName,
            @RequestBody CreateOrderReq order
    ) {
        final LimitOrder createdOrder = matchingEngine.addOrder(accountId, stockName, order);
        return ResponseEntity.created(URI.create(createdOrder.getOrderId().toString())).build();
    }

    @PostMapping("/{stockName}/cancel")
    public void cancelOrder(
            @RequestHeader("accountId") UUID accountId,
            @PathVariable String stockName,
            @RequestBody CancelOrderReq order
    ) {
        matchingEngine.cancelOrder(accountId, stockName, order);
    }
}
