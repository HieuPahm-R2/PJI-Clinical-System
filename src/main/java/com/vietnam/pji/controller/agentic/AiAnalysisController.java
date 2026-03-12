package com.vietnam.pji.controller.agentic;

import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.dto.response.ResponseData;
import com.vietnam.pji.services.AiPredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("${api.prefix}")
@RequiredArgsConstructor
@Tag(name = "AI Analysis Controller")
public class AiAnalysisController {

    private final AiPredictionService aiPredictionService;

    /**
     * Doctor triggers AI risk analysis for an episode.
     * Aggregates all medical data and publishes it to Kafka asynchronously.
     * The result will be persisted and notified when the AI team responds.
     */
    @PostMapping("/episodes/{episodeId}/ai-analysis")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Request AI risk analysis for an episode (async via Kafka)")
    public ResponseData<Void> requestAnalysis(@PathVariable Long episodeId) {
        aiPredictionService.requestAnalysis(episodeId);
        return new ResponseData<>(HttpStatus.ACCEPTED.value(),
                "Analysis request submitted. Result will be available shortly.");
    }

    /**
     * Retrieve all AI predictions for a given episode (paginated, newest first).
     */
    @GetMapping("/episodes/{episodeId}/ai-predictions")
    @Operation(summary = "Get all AI predictions for an episode")
    public ResponseData<PaginationResultDTO> getPredictions(
            @PathVariable Long episodeId,
            Pageable pageable) {
        return new ResponseData<>(OK.value(), "Success",
                aiPredictionService.getByEpisode(episodeId, pageable));
    }
}
