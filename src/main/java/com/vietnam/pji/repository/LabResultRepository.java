package com.vietnam.pji.repository;

import com.vietnam.pji.model.medical.LabResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LabResultRepository extends JpaRepository<LabResult, Long>, JpaSpecificationExecutor<LabResult> {
    Page<LabResult> findByEpisodeId(Long episodeId, Pageable pageable);
}
