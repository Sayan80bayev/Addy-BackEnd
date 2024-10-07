package com.example.backend.config;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.PingMessage;

@Component
@Slf4j
public class WebSocketPingScheduler {

    private final WebSocketHandler webSocketHandler;

    public WebSocketPingScheduler(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    @Scheduled(fixedRate = 30000)
    public void sendPingMessages() {
        webSocketHandler.getUserSessions().forEach((userId, session) -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new PingMessage());
                }
            } catch (Exception e) {
                log.error("Failed to send ping to user: " + userId, e);
            }
        });
    }
}
