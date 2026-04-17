package com.vietnam.pji.services.impl;

import com.vietnam.pji.constant.PendingLabTaskStatus;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.agentic.PendingLabTask;
import com.vietnam.pji.model.medical.LabResult;
import com.vietnam.pji.model.medical.PjiEpisode;
import com.vietnam.pji.repository.EpisodeRepository;
import com.vietnam.pji.repository.LabResultRepository;
import com.vietnam.pji.repository.PendingLabTaskRepository;
import com.vietnam.pji.services.PendingLabTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PendingLabTaskServiceImpl implements PendingLabTaskService {

    private final PendingLabTaskRepository pendingLabTaskRepository;
    private final LabResultRepository labResultRepository;
    private final EpisodeRepository episodeRepository;

    /**
     * Maps completeness field keys from Python's completeness.py to the JSONB
     * storage location inside LabResult.
     * Format: "section" -> field name inside the JSONB array/map.
     * Sections: "hematology" (hematologyTests), "fluid" (fluidAnalysis),
     * "biochemical" (biochemicalData).
     */
    private static final Map<String, String[]> FIELD_TO_LAB_MAPPING = Map.ofEntries(
            Map.entry("serum_CRP", new String[]{"hematology", "crp"}),
            Map.entry("serum_ESR", new String[]{"hematology", "esr"}),
            Map.entry("serum_D_Dimer", new String[]{"hematology", "d_dimer"}),
            Map.entry("serum_IL6", new String[]{"hematology", "serum_il6"}),
            Map.entry("synovial_WBC", new String[]{"fluid", "synovial_wbc"}),
            Map.entry("synovial_PMN", new String[]{"fluid", "synovial_pmn"}),
            Map.entry("synovial_alpha_defensin", new String[]{"fluid", "alpha_defensin"}),
            Map.entry("synovial_LE", new String[]{"fluid", "leukocyte_esterase"}),
            Map.entry("renal_function", new String[]{"biochemical", "creatinine"}),
            Map.entry("liver_function", new String[]{"biochemical", "alt"})
    );

    @Override
    @Transactional(readOnly = true)
    public List<PendingLabTask> getMyPendingTasks(Long userId) {
        return pendingLabTaskRepository
                .findByAssignedToUserIdAndStatusOrderByCreatedAtDesc(userId, PendingLabTaskStatus.PENDING);
    }

    @Override
    @Transactional(readOnly = true)
    public long getMyPendingCount(Long userId) {
        return pendingLabTaskRepository.countByAssignedToUserIdAndStatus(userId, PendingLabTaskStatus.PENDING);
    }

    @Override
    @Transactional
    public void dismiss(Long taskId) {
        PendingLabTask task = pendingLabTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Pending task not found"));
        task.setStatus(PendingLabTaskStatus.DISMISSED);
        pendingLabTaskRepository.save(task);
    }

    @Override
    @Transactional
    public void fulfillByQuickEntry(Long taskId, Object value, String unit) {
        PendingLabTask task = pendingLabTaskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Pending task not found"));

        if (task.getStatus() != PendingLabTaskStatus.PENDING) {
            return;
        }

        Long episodeId = task.getEpisode().getId();
        String field = task.getField();
        String[] mapping = FIELD_TO_LAB_MAPPING.get(field);
        if (mapping == null) {
            log.warn("No lab mapping for field={}, skipping quick entry", field);
            return;
        }

        // Get or create the latest lab result for this episode
        LabResult labResult = getOrCreateLatestLabResult(episodeId);

        // Write the value into the correct JSONB section
        Map<String, Object> testEntry = new LinkedHashMap<>();
        testEntry.put("name", mapping[1]);
        testEntry.put("value", value);
        if (unit != null) {
            testEntry.put("unit", unit);
        }

        String section = mapping[0];
        switch (section) {
            case "hematology" -> {
                List<Map<String, Object>> tests = labResult.getHematologyTests();
                if (tests == null) tests = new ArrayList<>();
                removeExisting(tests, mapping[1]);
                tests.add(testEntry);
                labResult.setHematologyTests(tests);
            }
            case "fluid" -> {
                List<Map<String, Object>> tests = labResult.getFluidAnalysis();
                if (tests == null) tests = new ArrayList<>();
                removeExisting(tests, mapping[1]);
                tests.add(testEntry);
                labResult.setFluidAnalysis(tests);
            }
            case "biochemical" -> {
                Map<String, Object> data = labResult.getBiochemicalData();
                if (data == null) data = new LinkedHashMap<>();
                data.put(mapping[1], testEntry);
                labResult.setBiochemicalData(data);
            }
        }

        LabResult saved = labResultRepository.save(labResult);

        // Mark task as fulfilled
        task.setStatus(PendingLabTaskStatus.FULFILLED);
        task.setFulfilledLabResult(saved);
        pendingLabTaskRepository.save(task);

        log.info("Quick-entry fulfilled: taskId={}, field={}, episodeId={}", taskId, field, episodeId);
    }

    @Override
    @Transactional
    public void createFromCompleteness(Long episodeId, Long patientId, Long userId,
                                       Long runId, List<Map<String, Object>> missingItems) {
        if (missingItems == null || missingItems.isEmpty()) return;

        PjiEpisode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));

        for (Map<String, Object> item : missingItems) {
            String field = (String) item.get("field");
            String category = (String) item.get("category");
            String importance = (String) item.get("importance");
            String message = (String) item.get("message");

            // Skip if a PENDING task already exists for this episode+field
            Optional<PendingLabTask> existing = pendingLabTaskRepository
                    .findByEpisodeIdAndFieldAndStatus(episodeId, field, PendingLabTaskStatus.PENDING);
            if (existing.isPresent()) continue;

            PendingLabTask task = PendingLabTask.builder()
                    .episode(episode)
                    .patient(episode.getPatient())
                    .assignedToUserId(userId)
                    .field(field)
                    .category(category)
                    .importance(importance)
                    .message(message)
                    .status(PendingLabTaskStatus.PENDING)
                    .createdFromRunId(runId)
                    .build();

            pendingLabTaskRepository.save(task);
        }

        log.info("Created pending tasks from completeness: episodeId={}, runId={}, count={}",
                episodeId, runId, missingItems.size());
    }

    @Override
    @Transactional
    public void autoFulfillForEpisode(Long episodeId, Long labResultId,
                                      List<Map<String, Object>> hematologyTests,
                                      List<Map<String, Object>> fluidAnalysis,
                                      Map<String, Object> biochemicalData) {
        List<PendingLabTask> pendingTasks = pendingLabTaskRepository
                .findByEpisodeIdAndStatus(episodeId, PendingLabTaskStatus.PENDING);

        if (pendingTasks.isEmpty()) return;

        Set<String> presentFields = extractPresentFields(hematologyTests, fluidAnalysis, biochemicalData);

        LabResult labResult = labResultRepository.findById(labResultId).orElse(null);

        for (PendingLabTask task : pendingTasks) {
            String[] mapping = FIELD_TO_LAB_MAPPING.get(task.getField());
            if (mapping != null && presentFields.contains(mapping[1])) {
                task.setStatus(PendingLabTaskStatus.FULFILLED);
                task.setFulfilledLabResult(labResult);
                pendingLabTaskRepository.save(task);
                log.info("Auto-fulfilled pending task: id={}, field={}", task.getId(), task.getField());
            }
        }
    }

    private LabResult getOrCreateLatestLabResult(Long episodeId) {
        var page = labResultRepository.findByEpisodeId(
                episodeId, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt")));

        if (!page.isEmpty()) {
            return page.getContent().get(0);
        }

        PjiEpisode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));
        LabResult newLab = LabResult.builder()
                .episode(episode)
                .hematologyTests(new ArrayList<>())
                .fluidAnalysis(new ArrayList<>())
                .biochemicalData(new LinkedHashMap<>())
                .build();
        return labResultRepository.save(newLab);
    }

    private Set<String> extractPresentFields(List<Map<String, Object>> hematologyTests,
                                             List<Map<String, Object>> fluidAnalysis,
                                             Map<String, Object> biochemicalData) {
        Set<String> fields = new HashSet<>();
        if (hematologyTests != null) {
            for (Map<String, Object> test : hematologyTests) {
                Object name = test.get("name");
                if (name != null && test.get("value") != null) {
                    fields.add(name.toString());
                }
            }
        }
        if (fluidAnalysis != null) {
            for (Map<String, Object> test : fluidAnalysis) {
                Object name = test.get("name");
                if (name != null && test.get("value") != null) {
                    fields.add(name.toString());
                }
            }
        }
        if (biochemicalData != null) {
            for (Map.Entry<String, Object> entry : biochemicalData.entrySet()) {
                if (entry.getValue() != null) {
                    fields.add(entry.getKey());
                }
            }
        }
        return fields;
    }

    private void removeExisting(List<Map<String, Object>> tests, String name) {
        tests.removeIf(t -> name.equals(t.get("name")));
    }
}
