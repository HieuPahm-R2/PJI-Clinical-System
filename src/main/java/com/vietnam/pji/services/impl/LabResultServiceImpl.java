package com.vietnam.pji.services.impl;

import com.vietnam.pji.dto.request.LabResultRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.medical.LabResult;
import com.vietnam.pji.model.medical.PjiEpisode;
import com.vietnam.pji.repository.EpisodeRepository;
import com.vietnam.pji.repository.LabResultRepository;
import com.vietnam.pji.services.LabResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LabResultServiceImpl implements LabResultService {

    private final LabResultRepository labResultRepository;
    private final EpisodeRepository episodeRepository;

    @Override
    public LabResult create(LabResultRequestDTO data) {
        PjiEpisode episode = episodeRepository.findById(data.getEpisodeId())
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));
        LabResult labResult = buildFromDto(data, new LabResult());
        labResult.setEpisode(episode);
        return labResultRepository.save(labResult);
    }

    @Override
    public LabResult update(Long id, LabResultRequestDTO data) {
        LabResult labResult = labResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab result not found"));
        buildFromDto(data, labResult);
        return labResultRepository.save(labResult);
    }

    @Override
    public LabResult getById(Long id) {
        return labResultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lab result not found"));
    }

    @Override
    public void delete(Long id) {
        if (!labResultRepository.existsById(id)) {
            throw new ResourceNotFoundException("Lab result not found");
        }
        labResultRepository.deleteById(id);
    }

    @Override
    public PaginationResultDTO getByEpisode(Long episodeId, Pageable pageable) {
        if (!episodeRepository.existsById(episodeId)) {
            throw new ResourceNotFoundException("Episode not found");
        }
        Page<LabResult> page = labResultRepository.findByEpisodeId(episodeId, pageable);
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

    private LabResult buildFromDto(LabResultRequestDTO data, LabResult labResult) {
        labResult.setEsr(data.getEsr());
        labResult.setWbcBlood(data.getWbcBlood());
        labResult.setNeut(data.getNeut());
        labResult.setMono(data.getMono());
        labResult.setLymph(data.getLymph());
        labResult.setEos(data.getEos());
        labResult.setBaso(data.getBaso());
        labResult.setRbc(data.getRbc());
        labResult.setHgb(data.getHgb());
        labResult.setHct(data.getHct());
        labResult.setRdw(data.getRdw());
        labResult.setIg(data.getIg());
        labResult.setMcv(data.getMcv());
        labResult.setMch(data.getMch());
        labResult.setMchc(data.getMchc());
        labResult.setCrp(data.getCrp());
        labResult.setSynovialWbc(data.getSynovialWbc());
        labResult.setSynovialPmn(data.getSynovialPmn());
        labResult.setBiochemicalData(data.getBiochemicalData());
        return labResult;
    }
}
