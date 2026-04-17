package com.vietnam.pji.services.impl;

import com.vietnam.pji.dto.request.SurgeryRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.medical.PjiEpisode;
import com.vietnam.pji.model.medical.Surgery;
import com.vietnam.pji.repository.EpisodeRepository;
import com.vietnam.pji.repository.SurgeryRepository;
import com.vietnam.pji.services.RedisService;
import com.vietnam.pji.services.SurgeryService;
import com.vietnam.pji.utils.mapper.SurgeryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SurgeryServiceImpl implements SurgeryService {

    private final SurgeryRepository surgeryRepository;
    private final EpisodeRepository episodeRepository;
    private final SurgeryMapper surgeryMapper;
    private final RedisService redisService;

    @Override
    public Surgery create(SurgeryRequestDTO data) {
        PjiEpisode episode = episodeRepository.findById(data.getEpisodeId())
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));
        Surgery surgery = surgeryMapper.toEntity(data);
        surgery.setEpisode(episode);
        Surgery saved = surgeryRepository.save(surgery);
        redisService.evictSnapshotCache(data.getEpisodeId());
        return saved;
    }

    @Override
    public Surgery update(Long id, SurgeryRequestDTO data) {
        Surgery surgery = surgeryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Surgery not found"));
        surgeryMapper.update(data, surgery);
        Surgery saved = surgeryRepository.save(surgery);
        redisService.evictSnapshotCache(surgery.getEpisode().getId());
        return saved;
    }

    @Override
    public Surgery getById(Long id) {
        return surgeryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Surgery not found"));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Surgery surgery = surgeryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Surgery not found"));
        Long episodeId = surgery.getEpisode().getId();
        surgeryRepository.deleteById(id);
        redisService.evictSnapshotCache(episodeId);
    }

    @Override
    public PaginationResultDTO getByEpisode(Long episodeId, Pageable pageable) {
        if (!episodeRepository.existsById(episodeId)) {
            throw new ResourceNotFoundException("Episode not found");
        }
        Page<Surgery> page = surgeryRepository.findByEpisodeId(episodeId, pageable);
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
