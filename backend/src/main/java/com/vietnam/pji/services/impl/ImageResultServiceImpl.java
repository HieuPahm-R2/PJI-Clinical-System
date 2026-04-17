package com.vietnam.pji.services.impl;

import com.vietnam.pji.dto.request.ImageResultRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.medical.ImageResult;
import com.vietnam.pji.model.medical.PjiEpisode;
import com.vietnam.pji.repository.EpisodeRepository;
import com.vietnam.pji.repository.ImageResultRepository;
import com.vietnam.pji.services.ImageResultService;
import com.vietnam.pji.services.RedisService;
import com.vietnam.pji.utils.mapper.ImageResultMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ImageResultServiceImpl implements ImageResultService {

    private final ImageResultRepository imageResultRepository;
    private final EpisodeRepository episodeRepository;
    private final ImageResultMapper imageResultMapper;
    private final RedisService redisService;

    @Override
    public ImageResult create(ImageResultRequestDTO data) {
        PjiEpisode episode = episodeRepository.findById(data.getEpisodeId())
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));
        ImageResult imageResult = imageResultMapper.toEntity(data);
        imageResult.setEpisode(episode);
        ImageResult saved = imageResultRepository.save(imageResult);
        redisService.evictSnapshotCache(data.getEpisodeId());
        return saved;
    }

    @Override
    public ImageResult update(Long id, ImageResultRequestDTO data) {
        ImageResult imageResult = imageResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image result not found"));
        imageResultMapper.update(data, imageResult);
        ImageResult saved = imageResultRepository.save(imageResult);
        redisService.evictSnapshotCache(imageResult.getEpisode().getId());
        return saved;
    }

    @Override
    public ImageResult getById(Long id) {
        return imageResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image result not found"));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ImageResult imageResult = imageResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image result not found"));
        Long episodeId = imageResult.getEpisode().getId();
        imageResultRepository.deleteById(id);
        redisService.evictSnapshotCache(episodeId);
    }

    @Override
    public PaginationResultDTO getByEpisode(Long episodeId, Pageable pageable) {
        if (!episodeRepository.existsById(episodeId)) {
            throw new ResourceNotFoundException("Episode not found");
        }
        Page<ImageResult> page = imageResultRepository.findByEpisodeId(episodeId, pageable);
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
