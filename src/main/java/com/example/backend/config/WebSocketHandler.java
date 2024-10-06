package com.example.backend.config;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.socket.TextMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    // Список для хранения активных WebSocket сессий
    private final Map<String, WebSocketSession> userSessions = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper(); // Для работы с JSON

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Connected: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received: " + payload);

        Map<String, String> messageData = objectMapper.readValue(payload, Map.class);

        String messageType = messageData.get("type");
        String userId = messageData.get("userId");

        if ("register".equals(messageType)) {
            userSessions.put(userId, session);
            log.info("Registered user: " + userId);
        } else if ("unsubscribe".equals(messageType)) {
            userSessions.remove(userId);
            log.info("Unsubscribed user: " + userId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        userSessions.values().remove(session);
        log.info("Disconnected: " + session.getId());
    }

    public void sendMessageToUser(String userId, String message) throws Exception {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(message));
        } else {
            log.info("User " + userId + " is not connected.");
        }
    }
}
