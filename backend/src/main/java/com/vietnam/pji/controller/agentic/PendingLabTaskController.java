package com.vietnam.pji.controller.agentic;

import com.vietnam.pji.dto.response.ResponseData;
import com.vietnam.pji.model.agentic.PendingLabTask;
import com.vietnam.pji.services.PendingLabTaskService;
import com.vietnam.pji.utils.SecurityUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}")
@Tag(name = "Pending Lab Tasks")
@RequiredArgsConstructor
public class PendingLabTaskController {

    private final PendingLabTaskService pendingLabTaskService;

    @GetMapping("/pending-lab-tasks/my")
    public ResponseData<List<PendingLabTask>> getMyPendingTasks() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<PendingLabTask> tasks = pendingLabTaskService.getMyPendingTasks(userId);
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch pending tasks", tasks);
    }

    @GetMapping("/pending-lab-tasks/my/count")
    public ResponseData<Long> getMyPendingCount() {
        Long userId = SecurityUtils.getCurrentUserId();
        long count = pendingLabTaskService.getMyPendingCount(userId);
        return new ResponseData<>(HttpStatus.OK.value(), "Pending task count", count);
    }

    @PostMapping("/pending-lab-tasks/{id}/dismiss")
    public ResponseData<Void> dismiss(@PathVariable Long id) {
        pendingLabTaskService.dismiss(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Task dismissed");
    }

    @PostMapping("/pending-lab-tasks/{id}/quick-entry")
    public ResponseData<Void> quickEntry(@PathVariable Long id,
                                         @RequestBody Map<String, Object> body) {
        Object value = body.get("value");
        String unit = body.get("unit") != null ? body.get("unit").toString() : null;
        pendingLabTaskService.fulfillByQuickEntry(id, value, unit);
        return new ResponseData<>(HttpStatus.OK.value(), "Lab value saved and task fulfilled");
    }

    @PostMapping("/episodes/{episodeId}/pending-lab-tasks/from-completeness")
    @ResponseStatus(HttpStatus.CREATED)
    @SuppressWarnings("unchecked")
    public ResponseData<Void> createFromCompleteness(
            @PathVariable Long episodeId,
            @RequestBody Map<String, Object> body) {
        Long userId = SecurityUtils.getCurrentUserId();
        Long patientId = body.get("patientId") != null
                ? Long.valueOf(body.get("patientId").toString()) : null;
        Long runId = body.get("runId") != null
                ? Long.valueOf(body.get("runId").toString()) : null;
        List<Map<String, Object>> missingItems = (List<Map<String, Object>>) body.get("missingItems");

        pendingLabTaskService.createFromCompleteness(episodeId, patientId, userId, runId, missingItems);
        return new ResponseData<>(HttpStatus.CREATED.value(), "Pending tasks created");
    }
}
