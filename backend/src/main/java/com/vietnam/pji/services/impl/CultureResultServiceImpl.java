package com.vietnam.pji.services.impl;

import com.vietnam.pji.dto.request.CultureResultRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.medical.CultureResult;
import com.vietnam.pji.model.medical.PjiEpisode;
import com.vietnam.pji.repository.EpisodeRepository;
import com.vietnam.pji.repository.medical.CultureResultRepository;
import com.vietnam.pji.services.CultureResultService;
import com.vietnam.pji.services.RedisService;
import com.vietnam.pji.utils.mapper.CultureResultMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CultureResultServiceImpl implements CultureResultService {

    private final CultureResultRepository cultureResultRepository;
    private final EpisodeRepository episodeRepository;
    private final CultureResultMapper cultureResultMapper;
    private final RedisService redisService;

    @Override
    public CultureResult create(CultureResultRequestDTO data) {
        PjiEpisode episode = episodeRepository.findById(data.getEpisodeId())
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));
        CultureResult cultureResult = cultureResultMapper.toEntity(data);
        cultureResult.setEpisode(episode);
        CultureResult saved = cultureResultRepository.save(cultureResult);
        redisService.evictSnapshotCache(data.getEpisodeId());
        return saved;
    }

    @Override
    public CultureResult update(Long id, CultureResultRequestDTO data) {
        CultureResult cultureResult = cultureResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Culture result not found"));
        cultureResultMapper.update(data, cultureResult);
        CultureResult saved = cultureResultRepository.save(cultureResult);
        redisService.evictSnapshotCache(cultureResult.getEpisode().getId());
        return saved;
    }

    @Override
    public CultureResult getById(Long id) {
        return cultureResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Culture result not found"));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CultureResult cultureResult = cultureResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Culture result not found"));
        Long episodeId = cultureResult.getEpisode().getId();
        cultureResultRepository.deleteById(id);
        redisService.evictSnapshotCache(episodeId);
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
}
