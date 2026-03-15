package com.vietnam.pji.dto.response;

import com.vietnam.pji.model.agentic.AiRagCitation;
import com.vietnam.pji.model.agentic.AiRecommendationItem;
import com.vietnam.pji.model.agentic.AiRecommendationRun;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRecommendationRunDetailDTO implements Serializable {

    private AiRecommendationRun run;
    private List<AiRecommendationItem> items;
    private List<AiRagCitation> citations;
}
