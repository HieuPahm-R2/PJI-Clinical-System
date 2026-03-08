package com.vietnam.pji.controller.medical;

import com.turkraft.springfilter.boot.Filter;
import com.vietnam.pji.dto.request.PatientRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.dto.response.ResponseData;
import com.vietnam.pji.model.medical.Patient;
import com.vietnam.pji.services.PatientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}")
@Validated
@Tag(name = "Patient Controller")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping("/patients")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Patient> createPatient(@Valid @RequestBody PatientRequestDTO request) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "Patient created successfully", patientService.create(request));
    }

    @PutMapping("/patients/{id}")
    public ResponseData<Patient> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientRequestDTO request) {
        return new ResponseData<>(HttpStatus.OK.value(), "Patient updated successfully", patientService.update(id, request));
    }

    @GetMapping("/patients/{id}")
    public ResponseData<Patient> getPatient(@PathVariable Long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch patient successfully", patientService.getById(id));
    }

    @DeleteMapping("/patients/{id}")
    public ResponseData<Void> deletePatient(@PathVariable Long id) {
        patientService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Patient deleted successfully");
    }

    @GetMapping("/patients")
    public ResponseData<PaginationResultDTO> getAllPatients(
            @Filter Specification<Patient> spec, Pageable pageable) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch patients successfully", patientService.getAll(spec, pageable));
    }
}
