package ru.test.sms.market;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TradingGatewaySimulatorTest {

    @Autowired
    private TradingGatewaySimulator tradingGatewaySimulator;

    @Autowired
    private TradeLedger tradeLedger;

    @Test
    void addOrdersTest0() throws InterruptedException {
        UUID accountId = UUID.randomUUID();
        String stockName = "AAPL";

        for (int i = 91; i <= 100; i++) {
            CreateOrderReq order = CreateOrderReq.builder()
                    .orderType(OrderType.BUY)
                    .count(5)
                    .price(i)
//                    .price(new Random().nextInt(20) + 80)
                    .build();

            tradingGatewaySimulator.addOrder(accountId, stockName, order);
            Thread.sleep(10);
        }
        Thread.sleep(2_000);

        List<Trade> trades1 = tradeLedger.getTrades();
        assertThat(trades1.size()).isEqualTo(0);

        for (int i = 0; i < 30; i++) {
            CreateOrderReq order = CreateOrderReq.builder()
                    .orderType(OrderType.SELL)
                    .count(3)
                    .price(95)
                    .build();
            tradingGatewaySimulator.addOrder(accountId, stockName, order);
            Thread.sleep(10);
        }

        Thread.sleep(2_000);

        List<Trade> trades2 = tradeLedger.getTrades();
        assertThat(trades2.size()).isEqualTo(12);

        for (int i = 0; i < 20; i++) {
            CreateOrderReq order = CreateOrderReq.builder()
                    .orderType(OrderType.BUY)
                    .count(5)
                    .price(80)
                    .build();
            tradingGatewaySimulator.addOrder(accountId, stockName, order);
            Thread.sleep(10);
        }

        Thread.sleep(2_000);

        List<Trade> trades3 = tradeLedger.getTrades();
        assertThat(trades3.size()).isEqualTo(13);
    }

}
