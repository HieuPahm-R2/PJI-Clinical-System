package com.vietnam.pji.repository;

import com.vietnam.pji.model.agentic.AiPrediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AiPredictionRepository extends JpaRepository<AiPrediction, Long>,
        JpaSpecificationExecutor<AiPrediction> {

    Page<AiPrediction> findByEpisodeId(Long episodeId, Pageable pageable);
}
