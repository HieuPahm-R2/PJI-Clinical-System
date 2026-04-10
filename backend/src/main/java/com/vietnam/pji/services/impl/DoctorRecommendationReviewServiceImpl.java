package com.vietnam.pji.services.impl;

import com.vietnam.pji.constant.ReviewStatus;
import com.vietnam.pji.dto.request.DoctorRecommendationReviewRequestDTO;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.agentic.AiRecommendationRun;
import com.vietnam.pji.model.agentic.DoctorRecommendationReview;
import com.vietnam.pji.model.medical.PjiEpisode;
import com.vietnam.pji.repository.AiRecommendationRunRepository;
import com.vietnam.pji.repository.DoctorRecommendationReviewRepository;
import com.vietnam.pji.repository.EpisodeRepository;
import com.vietnam.pji.services.DoctorRecommendationReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorRecommendationReviewServiceImpl implements DoctorRecommendationReviewService {

    private final DoctorRecommendationReviewRepository reviewRepository;
    private final AiRecommendationRunRepository runRepository;
    private final EpisodeRepository episodeRepository;

    @Override
    @Transactional
    public DoctorRecommendationReview createOrUpdateReview(Long episodeId,
            DoctorRecommendationReviewRequestDTO request) {
        PjiEpisode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found with id: " + episodeId));

        AiRecommendationRun run = runRepository.findById(request.getRunId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "AI Recommendation Run not found with id: " + request.getRunId()));

        ReviewStatus status = ReviewStatus.valueOf(request.getReviewStatus());

        // Upsert: update existing review for this run, or create new
        DoctorRecommendationReview review = reviewRepository.findByRunId(request.getRunId())
                .orElse(DoctorRecommendationReview.builder()
                        .episode(episode)
                        .run(run)
                        .build());

        review.setReviewStatus(status);
        review.setReviewNote(request.getReviewNote());
        review.setRejectionReason(request.getRejectionReason());

        review.setModificationJson(request.getModificationJson());

        return reviewRepository.save(review);
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorRecommendationReview getReviewByRunId(Long runId) {
        return reviewRepository.findByRunId(runId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorRecommendationReview> getReviewsByEpisodeId(Long episodeId) {
        return reviewRepository.findByEpisodeIdOrderByCreatedAtDesc(episodeId);
    }
}
