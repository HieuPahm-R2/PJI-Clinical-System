package com.vietnam.pji.services.impl;

import com.vietnam.pji.dto.request.SensitivityResultRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.medical.CultureResult;
import com.vietnam.pji.model.medical.SensitivityResult;
import com.vietnam.pji.repository.CultureResultRepository;
import com.vietnam.pji.repository.SensitivityResultRepository;
import com.vietnam.pji.services.SensitivityResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SensitivityResultServiceImpl implements SensitivityResultService {

    private final SensitivityResultRepository sensitivityResultRepository;
    private final CultureResultRepository cultureResultRepository;

    @Override
    public SensitivityResult create(SensitivityResultRequestDTO data) {
        CultureResult culture = cultureResultRepository.findById(data.getCultureId())
                .orElseThrow(() -> new ResourceNotFoundException("Culture result not found"));
        SensitivityResult result = buildFromDto(data, new SensitivityResult());
        result.setCulture(culture);
        return sensitivityResultRepository.save(result);
    }

    @Override
    public SensitivityResult update(Long id, SensitivityResultRequestDTO data) {
        SensitivityResult result = sensitivityResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensitivity result not found"));
        buildFromDto(data, result);
        return sensitivityResultRepository.save(result);
    }

    @Override
    public SensitivityResult getById(Long id) {
        return sensitivityResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sensitivity result not found"));
    }

    @Override
    public void delete(Long id) {
        if (!sensitivityResultRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sensitivity result not found");
        }
        sensitivityResultRepository.deleteById(id);
    }

    @Override
    public PaginationResultDTO getByCulture(Long cultureId, Pageable pageable) {
        if (!cultureResultRepository.existsById(cultureId)) {
            throw new ResourceNotFoundException("Culture result not found");
        }
        Page<SensitivityResult> page = sensitivityResultRepository.findByCultureId(cultureId, pageable);
        PaginationResultDTO.Meta meta = new PaginationResultDTO.Meta();
        meta.setPage(page.getNumber() + 1);
        meta.setPageSize(page.getSize());
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());

        PaginationResultDTO result = new PaginationResultDTO();
        result.setMeta(meta);
        result.setResult(page.getContent());
        return result;
    }

    private SensitivityResult buildFromDto(SensitivityResultRequestDTO data, SensitivityResult result) {
        result.setAntibioticName(data.getAntibioticName());
        result.setMicValue(data.getMicValue());
        result.setSensitivityCode(data.getSensitivityCode());
        return result;
    }
}
