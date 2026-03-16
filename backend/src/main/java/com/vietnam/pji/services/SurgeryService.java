package com.vietnam.pji.services;

import com.vietnam.pji.dto.request.SurgeryRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.model.medical.Surgery;
import org.springframework.data.domain.Pageable;

public interface SurgeryService {
    Surgery create(SurgeryRequestDTO data);
    Surgery update(Long id, SurgeryRequestDTO data);
    Surgery getById(Long id);
    void delete(Long id);
    PaginationResultDTO getByEpisode(Long episodeId, Pageable pageable);
}
