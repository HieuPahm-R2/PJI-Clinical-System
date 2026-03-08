package com.vietnam.pji.services.impl;

import com.vietnam.pji.dto.request.MedicalHistoryRequestDTO;
import com.vietnam.pji.exception.InvalidDataException;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.medical.MedicalHistory;
import com.vietnam.pji.model.medical.PjiEpisode;
import com.vietnam.pji.repository.EpisodeRepository;
import com.vietnam.pji.repository.MedicalHistoryRepository;
import com.vietnam.pji.services.MedicalHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    private final MedicalHistoryRepository medicalHistoryRepository;
    private final EpisodeRepository episodeRepository;

    @Override
    public MedicalHistory create(Long episodeId, MedicalHistoryRequestDTO data) {
        if (medicalHistoryRepository.existsByEpisodeId(episodeId)) {
            throw new InvalidDataException("Medical history already exists for this episode.");
        }
        PjiEpisode episode = episodeRepository.findById(episodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));

        MedicalHistory history = buildFromDto(data, new MedicalHistory());
        history.setEpisode(episode);
        return medicalHistoryRepository.save(history);
    }

    @Override
    public MedicalHistory update(Long episodeId, MedicalHistoryRequestDTO data) {
        MedicalHistory history = medicalHistoryRepository.findByEpisodeId(episodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Medical history not found for this episode"));
        buildFromDto(data, history);
        return medicalHistoryRepository.save(history);
    }

    @Override
    public MedicalHistory getByEpisodeId(Long episodeId) {
        return medicalHistoryRepository.findByEpisodeId(episodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Medical history not found for this episode"));
    }

    private MedicalHistory buildFromDto(MedicalHistoryRequestDTO data, MedicalHistory history) {
        history.setMedicalHistory(data.getMedicalHistory());
        history.setProcess(data.getProcess());
        history.setIsAllergy(data.getIsAllergy());
        history.setAllergyNote(data.getAllergyNote());
        history.setIsDrug(data.getIsDrug());
        history.setDrugNote(data.getDrugNote());
        history.setIsAlcohol(data.getIsAlcohol());
        history.setAlcoholNote(data.getAlcoholNote());
        history.setIsSmoking(data.getIsSmoking());
        history.setSmokingNote(data.getSmokingNote());
        history.setIsOther(data.getIsOther());
        history.setOtherNote(data.getOtherNote());
        return history;
    }
}
