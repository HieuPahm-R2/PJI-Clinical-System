package com.vietnam.pji.services;

import com.vietnam.pji.dto.request.SensitivityResultRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.model.medical.SensitivityResult;
import org.springframework.data.domain.Pageable;

public interface SensitivityResultService {
    SensitivityResult create(SensitivityResultRequestDTO data);
    SensitivityResult update(Long id, SensitivityResultRequestDTO data);
    SensitivityResult getById(Long id);
    void delete(Long id);
    PaginationResultDTO getByCulture(Long cultureId, Pageable pageable);
}
