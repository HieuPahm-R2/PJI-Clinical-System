package com.vietnam.pji.repository;

import com.vietnam.pji.model.agentic.AiRagCitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiRagCitationRepository extends JpaRepository<AiRagCitation, Long>,
        JpaSpecificationExecutor<AiRagCitation> {

    List<AiRagCitation> findByRunId(Long runId);

    List<AiRagCitation> findByItemId(Long itemId);
}
