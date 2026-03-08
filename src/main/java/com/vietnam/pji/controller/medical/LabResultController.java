package com.vietnam.pji.controller.medical;

import com.vietnam.pji.dto.request.LabResultRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.dto.response.ResponseData;
import com.vietnam.pji.model.medical.LabResult;
import com.vietnam.pji.services.LabResultService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}")
@Validated
@Tag(name = "Lab Result Controller")
@RequiredArgsConstructor
public class LabResultController {

    private final LabResultService labResultService;

    @PostMapping("/lab-results")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<LabResult> createLabResult(@Valid @RequestBody LabResultRequestDTO request) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Lab result created successfully",
                labResultService.create(request));
    }

    @PutMapping("/lab-results/{id}")
    public ResponseData<LabResult> updateLabResult(
            @PathVariable Long id, @Valid @RequestBody LabResultRequestDTO request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Lab result updated successfully",
                labResultService.update(id, request));
    }

    @GetMapping("/lab-results/{id}")
    public ResponseData<LabResult> getLabResult(@PathVariable Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch lab result successfully",
                labResultService.getById(id));
    }

    @DeleteMapping("/lab-results/{id}")
    public ResponseData<Void> deleteLabResult(@PathVariable Long id) {
        labResultService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Lab result deleted successfully");
    }

    @GetMapping("/episodes/{episodeId}/lab-results")
    public ResponseData<PaginationResultDTO> getLabResultsByEpisode(
            @PathVariable Long episodeId, Pageable pageable) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch lab results successfully",
                labResultService.getByEpisode(episodeId, pageable));
    }
}
