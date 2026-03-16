package com.vietnam.pji.services;

import com.vietnam.pji.dto.request.ImageResultRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.model.medical.ImageResult;
import org.springframework.data.domain.Pageable;

public interface ImageResultService {
    ImageResult create(ImageResultRequestDTO data);
    ImageResult update(Long id, ImageResultRequestDTO data);
    ImageResult getById(Long id);
    void delete(Long id);
    PaginationResultDTO getByEpisode(Long episodeId, Pageable pageable);
}
