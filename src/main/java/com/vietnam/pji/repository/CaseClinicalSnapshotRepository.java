package com.vietnam.pji.repository;

import com.vietnam.pji.model.agentic.CaseClinicalSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseClinicalSnapshotRepository extends JpaRepository<CaseClinicalSnapshot, Long>,
        JpaSpecificationExecutor<CaseClinicalSnapshot> {

    @Query("SELECT COALESCE(MAX(s.snapshotNo), 0) FROM CaseClinicalSnapshot s WHERE s.episode.id = :episodeId")
    int findMaxSnapshotNoByEpisodeId(Long episodeId);
}
