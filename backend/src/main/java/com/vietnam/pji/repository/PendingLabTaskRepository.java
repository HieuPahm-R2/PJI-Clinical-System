package com.vietnam.pji.repository;

import com.vietnam.pji.constant.PendingLabTaskStatus;
import com.vietnam.pji.model.agentic.PendingLabTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PendingLabTaskRepository extends JpaRepository<PendingLabTask, Long>,
        JpaSpecificationExecutor<PendingLabTask> {

    List<PendingLabTask> findByAssignedToUserIdAndStatusOrderByCreatedAtDesc(
            Long userId, PendingLabTaskStatus status);

    List<PendingLabTask> findByEpisodeIdAndStatus(Long episodeId, PendingLabTaskStatus status);

    Optional<PendingLabTask> findByEpisodeIdAndFieldAndStatus(
            Long episodeId, String field, PendingLabTaskStatus status);

    long countByAssignedToUserIdAndStatus(Long userId, PendingLabTaskStatus status);
}
