package com.vietnam.pji.services;

import com.vietnam.pji.dto.request.MedicalHistoryRequestDTO;
import com.vietnam.pji.model.medical.MedicalHistory;

public interface MedicalHistoryService {
    MedicalHistory create(Long episodeId, MedicalHistoryRequestDTO data);
    MedicalHistory update(Long episodeId, MedicalHistoryRequestDTO data);
    MedicalHistory getByEpisodeId(Long episodeId);
}
