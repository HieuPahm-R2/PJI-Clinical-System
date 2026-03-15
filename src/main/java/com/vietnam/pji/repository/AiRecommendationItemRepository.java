package com.vietnam.pji.repository;

import com.vietnam.pji.model.agentic.AiRecommendationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiRecommendationItemRepository extends JpaRepository<AiRecommendationItem, Long>,
        JpaSpecificationExecutor<AiRecommendationItem> {

    List<AiRecommendationItem> findByRunIdOrderByPriorityOrderAsc(Long runId);
}
