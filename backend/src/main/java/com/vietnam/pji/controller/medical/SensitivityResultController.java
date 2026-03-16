package com.vietnam.pji.controller.medical;

import com.vietnam.pji.dto.request.SensitivityResultRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.dto.response.ResponseData;
import com.vietnam.pji.model.medical.SensitivityResult;
import com.vietnam.pji.services.SensitivityResultService;
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
@Tag(name = "Sensitivity Result Controller")
@RequiredArgsConstructor
public class SensitivityResultController {

    private final SensitivityResultService sensitivityResultService;

    @PostMapping("/sensitivity-results")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<SensitivityResult> createSensitivityResult(@Valid @RequestBody SensitivityResultRequestDTO request) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Sensitivity result created successfully",
                sensitivityResultService.create(request));
    }

    @PutMapping("/sensitivity-results/{id}")
    public ResponseData<SensitivityResult> updateSensitivityResult(
            @PathVariable Long id, @Valid @RequestBody SensitivityResultRequestDTO request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Sensitivity result updated successfully",
                sensitivityResultService.update(id, request));
    }

    @GetMapping("/sensitivity-results/{id}")
    public ResponseData<SensitivityResult> getSensitivityResult(@PathVariable Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch sensitivity result successfully",
                sensitivityResultService.getById(id));
    }

    @DeleteMapping("/sensitivity-results/{id}")
    public ResponseData<Void> deleteSensitivityResult(@PathVariable Long id) {
        sensitivityResultService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Sensitivity result deleted successfully");
    }

    @GetMapping("/culture-results/{cultureId}/sensitivity-results")
    public ResponseData<PaginationResultDTO> getSensitivityResultsByCulture(
            @PathVariable Long cultureId, Pageable pageable) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch sensitivity results successfully",
                sensitivityResultService.getByCulture(cultureId, pageable));
    }
}
