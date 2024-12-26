package com.example.backend.service.messaging;

import com.example.backend.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageProducerImpl implements MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value("${base.url}")
    private String baseUrl;

    @Override
    public String getShortUrl(UUID uuid) {

        String message = baseUrl + "/view/" + uuid.toString();

        try {
            Object result = rabbitTemplate.convertSendAndReceive(
                    RabbitMQConfig.EXCHANGE_NAME,
                    "request.adCreated",
                    message);
            return  result.toString() ;
        }catch (Exception e){
            log.error(e.getMessage(), e);
            return "";
        }
    }
}
