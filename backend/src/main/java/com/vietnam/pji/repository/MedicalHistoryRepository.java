package com.vietnam.pji.repository;

import com.vietnam.pji.model.medical.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    Optional<MedicalHistory> findByEpisodeId(Long episodeId);
    boolean existsByEpisodeId(Long episodeId);
}
