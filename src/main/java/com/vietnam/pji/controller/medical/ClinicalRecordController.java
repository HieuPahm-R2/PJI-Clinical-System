package com.vietnam.pji.controller.medical;

import com.vietnam.pji.dto.request.ClinicalRecordRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.dto.response.ResponseData;
import com.vietnam.pji.model.medical.ClinicalRecord;
import com.vietnam.pji.services.ClinicalRecordService;
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
@Tag(name = "Clinical Record Controller")
@RequiredArgsConstructor
public class ClinicalRecordController {

    private final ClinicalRecordService clinicalRecordService;

    @PostMapping("/clinical-records")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<ClinicalRecord> createClinicalRecord(@Valid @RequestBody ClinicalRecordRequestDTO request) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Clinical record created successfully",
                clinicalRecordService.create(request));
    }

    @PutMapping("/clinical-records/{id}")
    public ResponseData<ClinicalRecord> updateClinicalRecord(
            @PathVariable Long id, @Valid @RequestBody ClinicalRecordRequestDTO request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Clinical record updated successfully",
                clinicalRecordService.update(id, request));
    }

    @GetMapping("/clinical-records/{id}")
    public ResponseData<ClinicalRecord> getClinicalRecord(@PathVariable Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch clinical record successfully",
                clinicalRecordService.getById(id));
    }

    @DeleteMapping("/clinical-records/{id}")
    public ResponseData<Void> deleteClinicalRecord(@PathVariable Long id) {
        clinicalRecordService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Clinical record deleted successfully");
    }

    @GetMapping("/episodes/{episodeId}/clinical-records")
    public ResponseData<PaginationResultDTO> getClinicalRecordsByEpisode(
            @PathVariable Long episodeId, Pageable pageable) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch clinical records successfully",
                clinicalRecordService.getByEpisode(episodeId, pageable));
    }
}
