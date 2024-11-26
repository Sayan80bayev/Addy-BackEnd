package com.example.backend.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String AD_CREATED_QUEUE = "adCreatedQueue";
    public static final String EXCHANGE_NAME = "adCreatedExchange";

    @Bean
    public Queue instructorDetailsQueue() {
        return new Queue(AD_CREATED_QUEUE, true);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding instructorDetailsBinding(Queue instructorDetailsQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(instructorDetailsQueue).to(topicExchange).with("request.adCreated");
    }

    @Bean
    Jackson2JsonMessageConverter converter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setNullAsOptionalEmpty(true);
        return converter;
    }
}