package com.vietnam.pji.services.impl;

import com.vietnam.pji.dto.request.SensitivityResultRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.medical.CultureResult;
import com.vietnam.pji.model.medical.SensitivityResult;
import com.vietnam.pji.repository.SensitivityResultRepository;
import com.vietnam.pji.repository.medical.CultureResultRepository;
import com.vietnam.pji.services.RedisService;
import com.vietnam.pji.services.SensitivityResultService;
import com.vietnam.pji.utils.mapper.SensitivityResultMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SensitivityResultServiceImpl implements SensitivityResultService {

    private final SensitivityResultRepository sensitivityResultRepository;
    private final CultureResultRepository cultureResultRepository;
    private final SensitivityResultMapper sensitivityResultMapper;
    private final RedisService redisService;

    @Override
    public SensitivityResult create(SensitivityResultRequestDTO data) {
        CultureResult culture = cultureResultRepository.findById(data.getCultureId())
                .orElseThrow(() -> new ResourceNotFoundException("Culture result not found"));
        SensitivityResult result = sensitivityResultMapper.toEntity(data);
        result.setCulture(culture);
        SensitivityResult saved = sensitivityResultRepository.save(result);
        // Evict snapshot: sensitivity → culture → episode
        redisService.evictSnapshotCache(culture.getEpisode().getId());
        return saved;
    }

    @Override
    @Transactional
    public SensitivityResult update(Long id, SensitivityResultRequestDTO data) {
        SensitivityResult result = sensitivityResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensitivity result not found"));
        sensitivityResultMapper.update(data, result);
        SensitivityResult saved = sensitivityResultRepository.save(result);
        // Navigate: sensitivity → culture → episode
        CultureResult culture = cultureResultRepository.findById(result.getCulture().getId())
                .orElse(null);
        if (culture != null) {
            redisService.evictSnapshotCache(culture.getEpisode().getId());
        }
        return saved;
    }

    @Override
    public SensitivityResult getById(Long id) {
        return sensitivityResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensitivity result not found"));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SensitivityResult result = sensitivityResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensitivity result not found"));
        // Get episodeId before deleting
        CultureResult culture = cultureResultRepository.findById(result.getCulture().getId())
                .orElse(null);
        Long episodeId = (culture != null) ? culture.getEpisode().getId() : null;
        sensitivityResultRepository.deleteById(id);
        if (episodeId != null) {
            redisService.evictSnapshotCache(episodeId);
        }
    }

    @Override
    public PaginationResultDTO getByCulture(Long cultureId, Pageable pageable) {
        if (!cultureResultRepository.existsById(cultureId)) {
            throw new ResourceNotFoundException("Culture result not found");
        }
        Page<SensitivityResult> page = sensitivityResultRepository.findByCultureId(cultureId, pageable);
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
