package ru.test.sms.market;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class MarketWebSocket extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> clients = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        clients.put(session.getId(), session);
        log.info("Client {} has been connected", session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        clients.remove(session.getId());
        log.info("Client {} has been disconnected", session.getId());
    }

    public void sendMessage(String message) {
        clients.values().forEach(session -> {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                log.error("Message has not been sent", e);
            }
        });
    }
}
