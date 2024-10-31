package com.example.notification.Config;

import com.example.notification.Model.Notification;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    private final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // Extract userId from the WebSocket handshake headers or URI if needed
        String userIdHeader = session.getHandshakeHeaders().getFirst("userId");
        if (userIdHeader != null) {
            Long userId = Long.valueOf(userIdHeader);
            sessions.put(userId, session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // Extract userId from the WebSocket handshake headers or URI if needed
        String userIdHeader = session.getHandshakeHeaders().getFirst("userId");
        if (userIdHeader != null) {
            Long userId = Long.valueOf(userIdHeader);
            sessions.remove(userId);
        }
    }
    public Map<Long, WebSocketSession> getSessions() {
        return sessions;
    }

}
