package com.vietnam.pji.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Kafka inbound payload — response from the Agentic RAG team.
 * This is deserialized from the pji.ai.analysis.response topic.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiPredictionResponseDTO implements Serializable {

    private Long episodeId;

    /** 'High', 'Medium', 'Low' */
    private String riskLevel;

    /** 0.00 – 100.00 */
    private BigDecimal infectionProbability;

    /** Raw JSON string containing the AI reasoning details */
    private String reasoningJson;
}
