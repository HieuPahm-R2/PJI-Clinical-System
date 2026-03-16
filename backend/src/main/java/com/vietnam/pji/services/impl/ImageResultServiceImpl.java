package com.vietnam.pji.services.impl;

import com.vietnam.pji.dto.request.ImageResultRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.medical.ImageResult;
import com.vietnam.pji.model.medical.PjiEpisode;
import com.vietnam.pji.repository.EpisodeRepository;
import com.vietnam.pji.repository.ImageResultRepository;
import com.vietnam.pji.services.ImageResultService;
import com.vietnam.pji.utils.mapper.ImageResultMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageResultServiceImpl implements ImageResultService {

    private final ImageResultRepository imageResultRepository;
    private final EpisodeRepository episodeRepository;
    private final ImageResultMapper imageResultMapper;

    @Override
    public ImageResult create(ImageResultRequestDTO data) {
        PjiEpisode episode = episodeRepository.findById(data.getEpisodeId())
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));
        ImageResult imageResult = imageResultMapper.toEntity(data);
        imageResult.setEpisode(episode);
        return imageResultRepository.save(imageResult);
    }

    @Override
    public ImageResult update(Long id, ImageResultRequestDTO data) {
        ImageResult imageResult = imageResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image result not found"));
        imageResultMapper.update(data, imageResult);
        return imageResultRepository.save(imageResult);
    }

    @Override
    public ImageResult getById(Long id) {
        return imageResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image result not found"));
    }

    @Override
    public void delete(Long id) {
        if (!imageResultRepository.existsById(id)) {
            throw new ResourceNotFoundException("Image result not found");
        }
        imageResultRepository.deleteById(id);
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
