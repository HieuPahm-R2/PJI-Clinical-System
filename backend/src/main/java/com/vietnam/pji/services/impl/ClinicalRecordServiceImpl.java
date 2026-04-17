package com.vietnam.pji.services.impl;

import com.vietnam.pji.dto.request.ClinicalRecordRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.medical.ClinicalRecord;
import com.vietnam.pji.model.medical.PjiEpisode;
import com.vietnam.pji.repository.EpisodeRepository;
import com.vietnam.pji.repository.medical.ClinicalRecordRepository;
import com.vietnam.pji.services.ClinicalRecordService;
import com.vietnam.pji.services.RedisService;
import com.vietnam.pji.utils.mapper.ClinicalRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClinicalRecordServiceImpl implements ClinicalRecordService {

    private final ClinicalRecordRepository clinicalRecordRepository;
    private final EpisodeRepository episodeRepository;
    private final ClinicalRecordMapper clinicalRecordMapper;
    private final RedisService redisService;

    @Override
    public ClinicalRecord create(ClinicalRecordRequestDTO data) {
        PjiEpisode episode = episodeRepository.findById(data.getEpisodeId())
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));
        ClinicalRecord record = clinicalRecordMapper.toEntity(data);
        record.setEpisode(episode);
        ClinicalRecord saved = clinicalRecordRepository.save(record);
        redisService.evictSnapshotCache(data.getEpisodeId());
        return saved;
    }

    @Override
    public ClinicalRecord update(Long id, ClinicalRecordRequestDTO data) {
        ClinicalRecord record = clinicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinical record not found"));
        clinicalRecordMapper.update(data, record);
        ClinicalRecord saved = clinicalRecordRepository.save(record);
        redisService.evictSnapshotCache(record.getEpisode().getId());
        return saved;
    }

    @Override
    public ClinicalRecord getById(Long id) {
        return clinicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinical record not found"));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ClinicalRecord record = clinicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinical record not found"));
        Long episodeId = record.getEpisode().getId();
        clinicalRecordRepository.deleteById(id);
        redisService.evictSnapshotCache(episodeId);
    }

    @Override
    public PaginationResultDTO getByEpisode(Long episodeId, Pageable pageable) {
        if (!episodeRepository.existsById(episodeId)) {
            throw new ResourceNotFoundException("Episode not found");
        }
        Page<ClinicalRecord> page = clinicalRecordRepository.findByEpisodeId(episodeId, pageable);
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
