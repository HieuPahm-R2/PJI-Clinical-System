package com.vietnam.pji.services.impl;

import com.vietnam.pji.dto.request.CultureResultRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.medical.CultureResult;
import com.vietnam.pji.model.medical.PjiEpisode;
import com.vietnam.pji.repository.CultureResultRepository;
import com.vietnam.pji.repository.EpisodeRepository;
import com.vietnam.pji.services.CultureResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CultureResultServiceImpl implements CultureResultService {

    private final CultureResultRepository cultureResultRepository;
    private final EpisodeRepository episodeRepository;

    @Override
    public CultureResult create(CultureResultRequestDTO data) {
        PjiEpisode episode = episodeRepository.findById(data.getEpisodeId())
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));
        CultureResult cultureResult = buildFromDto(data, new CultureResult());
        cultureResult.setEpisode(episode);
        return cultureResultRepository.save(cultureResult);
    }

    @Override
    public CultureResult update(Long id, CultureResultRequestDTO data) {
        CultureResult cultureResult = cultureResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Culture result not found"));
        buildFromDto(data, cultureResult);
        return cultureResultRepository.save(cultureResult);
    }

    @Override
    public CultureResult getById(Long id) {
        return cultureResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Culture result not found"));
    }

    @Override
    public void delete(Long id) {
        if (!cultureResultRepository.existsById(id)) {
            throw new ResourceNotFoundException("Culture result not found");
        }
        cultureResultRepository.deleteById(id);
    }

    @Override
    public PaginationResultDTO getByEpisode(Long episodeId, Pageable pageable) {
        if (!episodeRepository.existsById(episodeId)) {
            throw new ResourceNotFoundException("Episode not found");
        }
        Page<CultureResult> page = cultureResultRepository.findByEpisodeId(episodeId, pageable);
        PaginationResultDTO.Meta meta = new PaginationResultDTO.Meta();
        meta.setPage(page.getNumber() + 1);
        meta.setPageSize(page.getSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        PaginationResultDTO result = new PaginationResultDTO();
        result.setMeta(meta);
        result.setResult(page.getContent());
        return result;
    }

    private CultureResult buildFromDto(CultureResultRequestDTO data, CultureResult cultureResult) {
        cultureResult.setSampleType(data.getSampleType());
        cultureResult.setIncubationDays(data.getIncubationDays());
        cultureResult.setOrganismName(data.getOrganismName());
        cultureResult.setResult(data.getResult());
        cultureResult.setGramType(data.getGramType());
        cultureResult.setNotes(data.getNotes());
        return cultureResult;
    }
}
