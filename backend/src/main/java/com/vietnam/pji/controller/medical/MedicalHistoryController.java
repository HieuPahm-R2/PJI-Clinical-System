package com.vietnam.pji.controller.medical;

import com.vietnam.pji.dto.request.MedicalHistoryRequestDTO;
import com.vietnam.pji.dto.response.ResponseData;
import com.vietnam.pji.model.medical.MedicalHistory;
import com.vietnam.pji.services.MedicalHistoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/episodes/{episodeId}/medical-history")
@Validated
@Tag(name = "Medical History Controller")
@RequiredArgsConstructor
public class MedicalHistoryController {

    private final MedicalHistoryService medicalHistoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<MedicalHistory> createMedicalHistory(
            @PathVariable Long episodeId,
            @RequestBody MedicalHistoryRequestDTO request) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Medical history created successfully",
                medicalHistoryService.create(episodeId, request));
    }

    @PutMapping
    public ResponseData<MedicalHistory> updateMedicalHistory(
            @PathVariable Long episodeId,
            @RequestBody MedicalHistoryRequestDTO request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Medical history updated successfully",
                medicalHistoryService.update(episodeId, request));
    }

    @GetMapping
    public ResponseData<MedicalHistory> getMedicalHistory(@PathVariable Long episodeId) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch medical history successfully",
                medicalHistoryService.getByEpisodeId(episodeId));
    }
}
