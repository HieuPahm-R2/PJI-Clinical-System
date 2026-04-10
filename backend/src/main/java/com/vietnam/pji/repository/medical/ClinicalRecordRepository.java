package com.vietnam.pji.repository.medical;

import com.vietnam.pji.model.medical.ClinicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ClinicalRecordRepository
        extends JpaRepository<ClinicalRecord, Long>, JpaSpecificationExecutor<ClinicalRecord> {
    Page<ClinicalRecord> findByEpisodeId(Long episodeId, Pageable pageable);

    Optional<ClinicalRecord> findFirstByEpisodeIdOrderByCreatedAtDesc(Long episodeId);
}
