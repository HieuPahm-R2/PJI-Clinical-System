package com.vietnam.pji.services;

import com.vietnam.pji.dto.request.CultureResultRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.model.medical.CultureResult;
import org.springframework.data.domain.Pageable;

public interface CultureResultService {
    CultureResult create(CultureResultRequestDTO data);
    CultureResult update(Long id, CultureResultRequestDTO data);
    CultureResult getById(Long id);
    void delete(Long id);
    PaginationResultDTO getByEpisode(Long episodeId, Pageable pageable);
}
