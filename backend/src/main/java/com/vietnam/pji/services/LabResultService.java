package com.vietnam.pji.services;

import com.vietnam.pji.dto.request.LabResultRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.model.medical.LabResult;
import org.springframework.data.domain.Pageable;

public interface LabResultService {
    LabResult create(LabResultRequestDTO data);
    LabResult update(Long id, LabResultRequestDTO data);
    LabResult getById(Long id);
    void delete(Long id);
    PaginationResultDTO getByEpisode(Long episodeId, Pageable pageable);
}
