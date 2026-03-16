package com.vietnam.pji.repository;

import com.vietnam.pji.model.agentic.AiChatSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AiChatSessionRepository extends JpaRepository<AiChatSession, Long>,
        JpaSpecificationExecutor<AiChatSession> {

    Page<AiChatSession> findByEpisodeIdOrderByCreatedAtDesc(Long episodeId, Pageable pageable);
}
