package ru.test.sms.app.market;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.test.sms.app.LimitOrder;

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

    @DeleteMapping("/{stockName}/{orderId}")
    public void cancelOrder(
            @RequestHeader("accountId") UUID accountId,
            @PathVariable String stockName,
            @PathVariable UUID orderId
    ) {
        matchingEngine.cancelOrder(accountId, stockName, orderId);
    }
}
