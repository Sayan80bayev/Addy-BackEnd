package com.example.backend.config;

import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.backend.model.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.PongMessage;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> userSessions = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Connected: " + session.getId());
        session.sendMessage(new PingMessage());
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

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        log.info("Pong received from session: " + session.getId());
    }

    public void sendMessageToUser(String userId, Notification notification) throws Exception {
        WebSocketSession session = userSessions.get(userId);
        if (session != null && session.isOpen()) {
            session.sendMessage(new TextMessage(notification.toString()));
        } else {
            log.info("User " + userId + " is not connected.");
        }
    }

    public Map<String, WebSocketSession> getUserSessions() {
        return this.userSessions;
    }

}
