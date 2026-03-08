package com.vietnam.pji.services.impl;

import com.vietnam.pji.dto.request.ClinicalRecordRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.medical.ClinicalRecord;
import com.vietnam.pji.model.medical.PjiEpisode;
import com.vietnam.pji.repository.ClinicalRecordRepository;
import com.vietnam.pji.repository.EpisodeRepository;
import com.vietnam.pji.services.ClinicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClinicalRecordServiceImpl implements ClinicalRecordService {

    private final ClinicalRecordRepository clinicalRecordRepository;
    private final EpisodeRepository episodeRepository;

    @Override
    public ClinicalRecord create(ClinicalRecordRequestDTO data) {
        PjiEpisode episode = episodeRepository.findById(data.getEpisodeId())
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));
        ClinicalRecord record = buildFromDto(data, new ClinicalRecord());
        record.setEpisode(episode);
        return clinicalRecordRepository.save(record);
    }

    @Override
    public ClinicalRecord update(Long id, ClinicalRecordRequestDTO data) {
        ClinicalRecord record = clinicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinical record not found"));
        buildFromDto(data, record);
        return clinicalRecordRepository.save(record);
    }

    @Override
    public ClinicalRecord getById(Long id) {
        return clinicalRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinical record not found"));
    }

    @Override
    public void delete(Long id) {
        if (!clinicalRecordRepository.existsById(id)) {
            throw new ResourceNotFoundException("Clinical record not found");
        }
        clinicalRecordRepository.deleteById(id);
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

    private ClinicalRecord buildFromDto(ClinicalRecordRequestDTO data, ClinicalRecord record) {
        record.setOnIllness(data.getOnIllness());
        record.setTemperature(data.getTemperature());
        record.setBloodPressure(data.getBloodPressure());
        record.setHeartRate(data.getHeartRate());
        record.setRespiratoryRate(data.getRespiratoryRate());
        record.setBmi(data.getBmi());
        record.setLocalSymptoms(data.getLocalSymptoms());
        record.setNotations(data.getNotations());
        return record;
    }
}
