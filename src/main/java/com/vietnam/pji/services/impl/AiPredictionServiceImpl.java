package com.vietnam.pji.services.impl;

import com.vietnam.pji.dto.request.AiAnalysisRequestDTO;
import com.vietnam.pji.dto.response.AiPredictionResponseDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.kafka.AiAnalysisProducer;
import com.vietnam.pji.model.agentic.AiPrediction;
import com.vietnam.pji.model.medical.*;
import com.vietnam.pji.repository.*;
import com.vietnam.pji.services.AiPredictionService;
import com.vietnam.pji.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiPredictionServiceImpl implements AiPredictionService {

    private final EpisodeRepository episodeRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final ClinicalRecordRepository clinicalRecordRepository;
    private final SurgeryRepository surgeryRepository;
    private final LabResultRepository labResultRepository;
    private final ImageResultRepository imageResultRepository;
    private final CultureResultRepository cultureResultRepository;
    private final SensitivityResultRepository sensitivityResultRepository;
    private final AiPredictionRepository aiPredictionRepository;
    private final AiAnalysisProducer aiAnalysisProducer;

    @Override
    @Transactional(readOnly = true)
    public void requestAnalysis(Long episodeId) {
        if (!episodeRepository.existsById(episodeId)) {
            throw new ResourceNotFoundException("Episode not found: " + episodeId);
        }

        MedicalHistory medicalHistory = medicalHistoryRepository.findByEpisodeId(episodeId)
                .orElse(null);

        List<ClinicalRecord> clinicalRecords =
                clinicalRecordRepository.findByEpisodeId(episodeId, Pageable.unpaged()).getContent();

        List<Surgery> surgeries =
                surgeryRepository.findByEpisodeId(episodeId, Pageable.unpaged()).getContent();

        List<LabResult> labResults =
                labResultRepository.findByEpisodeId(episodeId, Pageable.unpaged()).getContent();

        List<ImageResult> imageResults =
                imageResultRepository.findByEpisodeId(episodeId, Pageable.unpaged()).getContent();

        List<CultureResult> cultureResults =
                cultureResultRepository.findByEpisodeId(episodeId, Pageable.unpaged()).getContent();

        // Collect all sensitivity results across every culture result in this episode
        List<Long> cultureIds = cultureResults.stream()
                .map(c -> c.getId())
                .toList();
        List<SensitivityResult> sensitivityResults = cultureIds.isEmpty()
                ? List.of()
                : sensitivityResultRepository.findByCultureIdIn(cultureIds);

        String requestedBy = SecurityUtils.getCurrentUserLogin().orElse("unknown");

        AiAnalysisRequestDTO payload = AiAnalysisRequestDTO.builder()
                .episodeId(episodeId)
                .requestedBy(requestedBy)
                .medicalHistory(medicalHistory)
                .clinicalRecords(clinicalRecords)
                .surgeries(surgeries)
                .labResults(labResults)
                .imageResults(imageResults)
                .cultureResults(cultureResults)
                .sensitivityResults(sensitivityResults)
                .build();

        aiAnalysisProducer.sendAnalysisRequest(payload);
        log.info("[AiPrediction] Analysis request dispatched for episode {} by {}", episodeId, requestedBy);
    }

    @Override
    public AiPrediction savePrediction(AiPredictionResponseDTO response) {
        PjiEpisode episode = episodeRepository.findById(response.getEpisodeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Episode not found: " + response.getEpisodeId()));

        AiPrediction prediction = AiPrediction.builder()
                .episode(episode)
                .riskLevel(response.getRiskLevel())
                .infectionProbability(response.getInfectionProbability())
                .reasoningJson(response.getReasoningJson())
                .build();

        return aiPredictionRepository.save(prediction);
    }

    @Override
    public PaginationResultDTO getByEpisode(Long episodeId, Pageable pageable) {
        Page<AiPrediction> page = aiPredictionRepository.findByEpisodeId(episodeId, pageable);

        PaginationResultDTO.Meta meta = new PaginationResultDTO.Meta();
        meta.setPage(page.getNumber() + 1);
        meta.setPageSize(page.getSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        PaginationResultDTO result = new PaginationResultDTO();
        result.setMeta(meta);
        result.setResult(page.getContent());
        return result;
    }
}
