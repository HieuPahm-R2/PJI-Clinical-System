package com.vietnam.pji.message;

import com.vietnam.pji.config.integration.RabbitMQConfig;
import com.vietnam.pji.dto.request.RabbitMQRecommendationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQPublisher {

        private final RabbitTemplate rabbitTemplate;

        public void publishRecommendationJob(RabbitMQRecommendationMessage message) {
                log.info("Publishing recommendation job to RabbitMQ: requestId={}, episodeId={}, triggerType={}",
                                message.getRequestId(), message.getEpisodeId(), message.getTriggerType());

                rabbitTemplate.convertAndSend(
                                RabbitMQConfig.EXCHANGE,
                                RabbitMQConfig.ROUTING_KEY_GENERATE,
                                message);
        }

        public void publishRefreshJob(RabbitMQRecommendationMessage message) {
                log.info("Publishing refresh job to RabbitMQ: requestId={}, episodeId={}",
                                message.getRequestId(), message.getEpisodeId());

                rabbitTemplate.convertAndSend(
                                RabbitMQConfig.EXCHANGE,
                                RabbitMQConfig.ROUTING_KEY_REFRESH,
                                message);
        }
}
