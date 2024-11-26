package com.example.backend.service.messaging;

import java.util.UUID;

public interface MessageProducer {
    String getShortUrl(UUID uuid);
}
