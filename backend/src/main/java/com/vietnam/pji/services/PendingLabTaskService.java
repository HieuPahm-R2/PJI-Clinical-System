package com.vietnam.pji.services;

import com.vietnam.pji.model.agentic.PendingLabTask;

import java.util.List;
import java.util.Map;

public interface PendingLabTaskService {

    List<PendingLabTask> getMyPendingTasks(Long userId);

    long getMyPendingCount(Long userId);

    void dismiss(Long taskId);

    void fulfillByQuickEntry(Long taskId, Object value, String unit);

    void createFromCompleteness(Long episodeId, Long patientId, Long userId,
                                Long runId, List<Map<String, Object>> missingItems);

    void autoFulfillForEpisode(Long episodeId, Long labResultId,
                               List<Map<String, Object>> hematologyTests,
                               List<Map<String, Object>> fluidAnalysis,
                               Map<String, Object> biochemicalData);
}
