package com.vietnam.pji.services;

import com.vietnam.pji.dto.request.EpisodeRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.model.medical.PjiEpisode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface EpisodeService {
    PjiEpisode create(EpisodeRequestDTO data);
    PjiEpisode update(Long id, EpisodeRequestDTO data);
    PjiEpisode getById(Long id);
    void delete(Long id);
    PaginationResultDTO getAll(Specification<PjiEpisode> spec, Pageable pageable);
    PaginationResultDTO getByPatient(Long patientId, Pageable pageable);
}
