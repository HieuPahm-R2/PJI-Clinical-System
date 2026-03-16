package com.vietnam.pji.controller.agentic;

import com.vietnam.pji.constant.TriggerType;
import com.vietnam.pji.dto.response.AiRecommendationRunDetailDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.dto.response.ResponseData;
import com.vietnam.pji.services.AiRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}")
@RequiredArgsConstructor
@Tag(name = "AI Recommendation Controller")
public class AiRecommendationController {

    private final AiRecommendationService aiRecommendationService;

    @PostMapping("/episodes/{episodeId}/ai-recommendations/generate")
    @Operation(summary = "Generate AI recommendation for an episode (sync)")
    public ResponseData<AiRecommendationRunDetailDTO> generateRecommendation(
            @PathVariable Long episodeId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Recommendation generated successfully",
                aiRecommendationService.generateRecommendation(episodeId, TriggerType.MANUAL_GENERATE));
    }

    @GetMapping("/episodes/{episodeId}/ai-recommendations/runs")
    @Operation(summary = "Get AI recommendation run history for an episode")
    public ResponseData<PaginationResultDTO> getRunHistory(
            @PathVariable Long episodeId, Pageable pageable) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch run history successfully",
                aiRecommendationService.getRunHistory(episodeId, pageable));
    }

    @GetMapping("/ai-recommendations/runs/{runId}")
    @Operation(summary = "Get detail of a specific AI recommendation run")
    public ResponseData<AiRecommendationRunDetailDTO> getRunDetail(@PathVariable Long runId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch run detail successfully",
                aiRecommendationService.getRunDetail(runId));
    }

    @PostMapping("/ai-recommendations/runs/{runId}/retry")
    @Operation(summary = "Retry a failed AI recommendation run")
    public ResponseData<AiRecommendationRunDetailDTO> retryRun(@PathVariable Long runId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Run retried successfully",
                aiRecommendationService.retryRun(runId));
    }
}
