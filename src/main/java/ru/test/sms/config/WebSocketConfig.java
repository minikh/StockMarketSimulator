package ru.test.sms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import ru.test.sms.market.MarketWebSocket;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final MarketWebSocket marketWebSocket;

    public WebSocketConfig(MarketWebSocket marketWebSocket) {
        this.marketWebSocket = marketWebSocket;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(marketWebSocket, "/ws/orders")
                .setAllowedOrigins("*");
    }
}
