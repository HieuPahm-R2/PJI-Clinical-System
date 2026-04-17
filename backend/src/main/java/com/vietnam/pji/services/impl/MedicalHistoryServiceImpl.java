package com.vietnam.pji.services.impl;

import com.vietnam.pji.dto.request.MedicalHistoryRequestDTO;
import com.vietnam.pji.exception.InvalidDataException;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.medical.MedicalHistory;
import com.vietnam.pji.model.medical.PjiEpisode;
import com.vietnam.pji.repository.EpisodeRepository;
import com.vietnam.pji.repository.MedicalHistoryRepository;
import com.vietnam.pji.services.MedicalHistoryService;
import com.vietnam.pji.services.RedisService;
import com.vietnam.pji.utils.mapper.MedicalHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    private final MedicalHistoryRepository medicalHistoryRepository;
    private final EpisodeRepository episodeRepository;
    private final MedicalHistoryMapper medicalHistoryMapper;
    private final RedisService redisService;

    @Override
    public MedicalHistory create(Long episodeId, MedicalHistoryRequestDTO data) {
        if (medicalHistoryRepository.existsByEpisodeId(episodeId)) {
            throw new InvalidDataException("Medical history already exists for this episode.");
        }
        PjiEpisode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));

        MedicalHistory history = medicalHistoryMapper.toEntity(data);
        history.setEpisode(episode);
        MedicalHistory saved = medicalHistoryRepository.save(history);
        redisService.evictSnapshotCache(episodeId);
        return saved;
    }

    @Override
    public MedicalHistory update(Long episodeId, MedicalHistoryRequestDTO data) {
        MedicalHistory history = medicalHistoryRepository.findByEpisodeId(episodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Medical history not found for this episode"));
        medicalHistoryMapper.update(data, history);
        MedicalHistory saved = medicalHistoryRepository.save(history);
        redisService.evictSnapshotCache(episodeId);
        return saved;
    }

    @Override
    public MedicalHistory getByEpisodeId(Long episodeId) {
        return medicalHistoryRepository.findByEpisodeId(episodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Medical history not found for this episode"));
    }
}
