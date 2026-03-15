package com.vietnam.pji.config.integration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "pji.ai.exchange";
    public static final String RECOMMENDATION_QUEUE = "pji.ai.recommendation.queue";
    public static final String RECOMMENDATION_DLQ = "pji.ai.recommendation.dlq";
    public static final String ROUTING_KEY_GENERATE = "ai.recommendation.generate";
    public static final String ROUTING_KEY_REFRESH = "ai.recommendation.refresh";

    @Bean
    public TopicExchange aiExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue recommendationQueue() {
        return QueueBuilder.durable(RECOMMENDATION_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", RECOMMENDATION_DLQ)
                .build();
    }

    @Bean
    public Queue recommendationDlq() {
        return QueueBuilder.durable(RECOMMENDATION_DLQ).build();
    }

    @Bean
    public Binding generateBinding(Queue recommendationQueue, TopicExchange aiExchange) {
        return BindingBuilder.bind(recommendationQueue)
                .to(aiExchange)
                .with(ROUTING_KEY_GENERATE);
    }

    @Bean
    public Binding refreshBinding(Queue recommendationQueue, TopicExchange aiExchange) {
        return BindingBuilder.bind(recommendationQueue)
                .to(aiExchange)
                .with(ROUTING_KEY_REFRESH);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
