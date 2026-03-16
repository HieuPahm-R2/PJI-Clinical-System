package com.vietnam.pji.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RabbitMQRecommendationMessage implements Serializable {

    private String requestId;
    private Long runId;
    private Long episodeId;
    private Long snapshotId;
    private String triggerType;
    private Long requestedBy;
}
