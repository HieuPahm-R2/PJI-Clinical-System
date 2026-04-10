package com.vietnam.pji.services;

import com.vietnam.pji.dto.request.DoctorRecommendationReviewRequestDTO;
import com.vietnam.pji.model.agentic.DoctorRecommendationReview;

import java.util.List;

public interface DoctorRecommendationReviewService {

    DoctorRecommendationReview createOrUpdateReview(Long episodeId, DoctorRecommendationReviewRequestDTO request);

    DoctorRecommendationReview getReviewByRunId(Long runId);

    List<DoctorRecommendationReview> getReviewsByEpisodeId(Long episodeId);
}
