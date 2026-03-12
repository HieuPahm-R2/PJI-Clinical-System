package com.vietnam.pji.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String TOPIC_AI_ANALYSIS_REQUEST  = "pji.ai.analysis.request";
    public static final String TOPIC_AI_ANALYSIS_RESPONSE = "pji.ai.analysis.response";

    @Bean
    public NewTopic aiAnalysisRequestTopic() {
        return TopicBuilder.name(TOPIC_AI_ANALYSIS_REQUEST)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic aiAnalysisResponseTopic() {
        return TopicBuilder.name(TOPIC_AI_ANALYSIS_RESPONSE)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
