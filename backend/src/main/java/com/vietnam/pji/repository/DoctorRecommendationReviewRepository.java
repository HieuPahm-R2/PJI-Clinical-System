package com.vietnam.pji.repository;

import com.vietnam.pji.model.agentic.DoctorRecommendationReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface DoctorRecommendationReviewRepository
        extends JpaRepository<DoctorRecommendationReview, Long>,
        JpaSpecificationExecutor<DoctorRecommendationReview> {

    Optional<DoctorRecommendationReview> findByRunId(Long runId);

    List<DoctorRecommendationReview> findByEpisodeIdOrderByCreatedAtDesc(Long episodeId);
}
