package com.vietnam.pji.repository;

import com.vietnam.pji.model.medical.ImageResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ImageResultRepository extends JpaRepository<ImageResult, Long>, JpaSpecificationExecutor<ImageResult> {
    Page<ImageResult> findByEpisodeId(Long episodeId, Pageable pageable);

    List<ImageResult> findByEpisodeIdOrderByCreatedAtDesc(Long episodeId);
}
