package com.vietnam.pji.config.integration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // --- Exchange ---
    public static final String EXCHANGE = "pji.ai.exchange";

    // --- Recommendation (Backend → Python) ---
    public static final String RECOMMENDATION_QUEUE = "pji.ai.recommendation.queue";
    public static final String RECOMMENDATION_DLQ = "pji.ai.recommendation.dlq";
    public static final String ROUTING_KEY_GENERATE = "ai.recommendation.generate";
    public static final String ROUTING_KEY_REFRESH = "ai.recommendation.refresh";

    // --- Recommendation Result (Python → Backend) ---
    public static final String RECOMMENDATION_RESULT_QUEUE = "pji.ai.recommendation.result.queue";
    public static final String ROUTING_KEY_RESULT = "ai.recommendation.result";

    // --- Chat (Backend → Python) ---
    public static final String CHAT_QUEUE = "pji.ai.chat.queue";
    public static final String CHAT_RESULT_QUEUE = "pji.ai.chat.result.queue";
    public static final String ROUTING_KEY_CHAT = "ai.chat.request";
    public static final String ROUTING_KEY_CHAT_RESULT = "ai.chat.result";

    // =====================================================================
    // Exchange
    // =====================================================================

    @Bean
    public TopicExchange aiExchange() {
        return new TopicExchange(EXCHANGE);
    }

    // =====================================================================
    // Recommendation queues & bindings
    // =====================================================================

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
    public Queue recommendationResultQueue() {
        return QueueBuilder.durable(RECOMMENDATION_RESULT_QUEUE).build();
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
    public Binding resultBinding(Queue recommendationResultQueue, TopicExchange aiExchange) {
        return BindingBuilder.bind(recommendationResultQueue)
                .to(aiExchange)
                .with(ROUTING_KEY_RESULT);
    }

    // =====================================================================
    // Chat queues & bindings
    // =====================================================================

    @Bean
    public Queue chatQueue() {
        return QueueBuilder.durable(CHAT_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", RECOMMENDATION_DLQ)
                .build();
    }

    @Bean
    public Queue chatResultQueue() {
        return QueueBuilder.durable(CHAT_RESULT_QUEUE).build();
    }

    @Bean
    public Binding chatBinding(Queue chatQueue, TopicExchange aiExchange) {
        return BindingBuilder.bind(chatQueue)
                .to(aiExchange)
                .with(ROUTING_KEY_CHAT);
    }

    @Bean
    public Binding chatResultBinding(Queue chatResultQueue, TopicExchange aiExchange) {
        return BindingBuilder.bind(chatResultQueue)
                .to(aiExchange)
                .with(ROUTING_KEY_CHAT_RESULT);
    }

    // =====================================================================
    // Message converter & template
    // =====================================================================

    @Bean
    public MessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();

        // Infer target type from @RabbitListener method parameter instead of
        // requiring the __TypeId__ header. This is necessary because the Python
        // RAG worker publishes plain JSON without Spring type headers.
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(DefaultJackson2JavaTypeMapper.TypePrecedence.INFERRED);
        converter.setJavaTypeMapper(typeMapper);

        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter);
        return template;
    }
}
