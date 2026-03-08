package com.vietnam.pji.repository;

import com.vietnam.pji.model.medical.CultureResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CultureResultRepository extends JpaRepository<CultureResult, Long>, JpaSpecificationExecutor<CultureResult> {
    Page<CultureResult> findByEpisodeId(Long episodeId, Pageable pageable);
}
