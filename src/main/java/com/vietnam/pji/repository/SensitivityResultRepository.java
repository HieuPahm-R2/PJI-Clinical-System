package com.vietnam.pji.repository;

import com.vietnam.pji.model.medical.SensitivityResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SensitivityResultRepository extends JpaRepository<SensitivityResult, Long>, JpaSpecificationExecutor<SensitivityResult> {
    Page<SensitivityResult> findByCultureId(Long cultureId, Pageable pageable);
}
