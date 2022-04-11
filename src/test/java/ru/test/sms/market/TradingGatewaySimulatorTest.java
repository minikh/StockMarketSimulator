package ru.test.sms.market;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.test.sms.market.account.Account;
import ru.test.sms.market.account.AccountService;
import ru.test.sms.market.order.CreateOrderReq;
import ru.test.sms.market.order.OrderType;
import ru.test.sms.market.trade.Trade;
import ru.test.sms.market.trade.TradeLedger;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TradingGatewaySimulatorTest {

    @Autowired
    private TradingGatewaySimulator tradingGatewaySimulator;

    @Autowired
    private TradeLedger tradeLedger;

    @Autowired
    private AccountService accountService;

    @Test
    void addOrdersTest0() throws InterruptedException {
        final Account buyerAccount = accountService.getAccount(UUID.randomUUID());
        final Account sellerAccount = accountService.getAccount(UUID.randomUUID());

        assertThat(buyerAccount.getBalance()).isEqualTo(accountService.startBalance);
        assertThat(buyerAccount.getReservedBalance()).isEqualTo(0);
        assertThat(sellerAccount.getBalance()).isEqualTo(accountService.startBalance);
        assertThat(sellerAccount.getReservedBalance()).isEqualTo(0);

        String stockName = "AAPL";

        for (int i = 81; i <= 100; i++) {
            CreateOrderReq order = CreateOrderReq.builder()
                    .orderType(OrderType.BUY)
                    .count(7)
                    .price(i)
//                    .price(new Random().nextInt(20) + 80)
                    .build();

            tradingGatewaySimulator.addOrder(buyerAccount.getAccountId(), stockName, order);
            Thread.sleep(20);
        }
        System.out.println();
        Thread.sleep(2_000);
        List<Trade> trades1 = tradeLedger.getTrades();
        assertThat(trades1.size()).isEqualTo(0);
        assertThat(buyerAccount.getBalance()).isNotEqualTo(accountService.startBalance);
        assertThat(buyerAccount.getReservedBalance()).isNotEqualTo(0);
        assertThat(sellerAccount.getBalance()).isEqualTo(accountService.startBalance);
        assertThat(sellerAccount.getReservedBalance()).isEqualTo(0);


        for (int i = 95; i > 75; i--) {
            CreateOrderReq order = CreateOrderReq.builder()
                    .orderType(OrderType.SELL)
                    .count(3)
                    .price(i)
                    .build();
            tradingGatewaySimulator.addOrder(sellerAccount.getAccountId(), stockName, order);

            Thread.sleep(20);
        }

        System.out.println();
        Thread.sleep(2_000);
        List<Trade> trades2 = tradeLedger.getTrades();
        assertThat(trades2.size()).isEqualTo(14);


        for (int i = 0; i < 20; i++) {
            CreateOrderReq order = CreateOrderReq.builder()
                    .orderType(OrderType.BUY)
                    .count(5)
                    .price(80)
                    .build();
            tradingGatewaySimulator.addOrder(buyerAccount.getAccountId(), stockName, order);
            Thread.sleep(20);
        }

        Thread.sleep(2_000);
        List<Trade> trades3 = tradeLedger.getTrades();
        assertThat(trades3.size()).isEqualTo(21);


        assertThat(buyerAccount.getBalance()).isNotEqualTo(accountService.startBalance);
        assertThat(sellerAccount.getBalance()).isNotEqualTo(accountService.startBalance);
    }

}
