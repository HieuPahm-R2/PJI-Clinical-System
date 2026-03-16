package com.vietnam.pji.repository;

import com.vietnam.pji.model.agentic.AiChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiChatMessageRepository extends JpaRepository<AiChatMessage, Long>,
        JpaSpecificationExecutor<AiChatMessage> {

    Page<AiChatMessage> findBySessionIdOrderByCreatedAtAsc(Long sessionId, Pageable pageable);

    List<AiChatMessage> findTop20BySessionIdOrderByCreatedAtDesc(Long sessionId);
}
