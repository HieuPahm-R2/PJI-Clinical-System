package com.vietnam.pji.services;

import com.vietnam.pji.dto.request.ClinicalRecordRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.model.medical.ClinicalRecord;
import org.springframework.data.domain.Pageable;

public interface ClinicalRecordService {
    ClinicalRecord create(ClinicalRecordRequestDTO data);
    ClinicalRecord update(Long id, ClinicalRecordRequestDTO data);
    ClinicalRecord getById(Long id);
    void delete(Long id);
    PaginationResultDTO getByEpisode(Long episodeId, Pageable pageable);
}
