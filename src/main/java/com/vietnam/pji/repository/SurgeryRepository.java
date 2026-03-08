package com.vietnam.pji.repository;

import com.vietnam.pji.model.medical.Surgery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SurgeryRepository extends JpaRepository<Surgery, Long>, JpaSpecificationExecutor<Surgery> {
    Page<Surgery> findByEpisodeId(Long episodeId, Pageable pageable);
}
