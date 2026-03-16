package com.vietnam.pji.services;

import com.vietnam.pji.dto.request.PatientRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.model.medical.Patient;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface PatientService {
    Patient create(PatientRequestDTO data);
    Patient update(Long id, PatientRequestDTO data);
    Patient getById(Long id);
    void delete(Long id);
    PaginationResultDTO getAll(Specification<Patient> spec, Pageable pageable);
}
