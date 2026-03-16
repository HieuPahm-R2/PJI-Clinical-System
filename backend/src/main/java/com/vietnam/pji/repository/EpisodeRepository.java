package com.vietnam.pji.repository;

import com.vietnam.pji.model.medical.PjiEpisode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EpisodeRepository extends JpaRepository<PjiEpisode, Long>, JpaSpecificationExecutor<PjiEpisode> {
    Page<PjiEpisode> findByPatientId(Long patientId, Pageable pageable);
}
