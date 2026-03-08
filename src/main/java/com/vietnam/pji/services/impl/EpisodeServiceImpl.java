package com.vietnam.pji.services.impl;

import com.vietnam.pji.dto.request.EpisodeRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.auth.User;
import com.vietnam.pji.model.medical.Patient;
import com.vietnam.pji.model.medical.PjiEpisode;
import com.vietnam.pji.repository.EpisodeRepository;
import com.vietnam.pji.repository.PatientRepository;
import com.vietnam.pji.repository.UserRepository;
import com.vietnam.pji.services.EpisodeService;
import com.vietnam.pji.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EpisodeServiceImpl implements EpisodeService {

    private final EpisodeRepository episodeRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;

    @Override
    public PjiEpisode create(EpisodeRequestDTO data) {
        Patient patient = patientRepository.findById(data.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        PjiEpisode episode = buildFromDto(data, new PjiEpisode());
        episode.setPatient(patient);

        // Assign creator from current authenticated user
        SecurityUtils.getCurrentUserLogin().ifPresent(email -> {
            User user = userRepository.findByEmail(email);
            episode.setCreatedBy(user);
        });

        return episodeRepository.save(episode);
    }

    @Override
    public PjiEpisode update(Long id, EpisodeRequestDTO data) {
        PjiEpisode episode = episodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));

        if (data.getPatientId() != null && !data.getPatientId().equals(episode.getPatient().getId())) {
            Patient patient = patientRepository.findById(data.getPatientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
            episode.setPatient(patient);
        }

        buildFromDto(data, episode);
        return episodeRepository.save(episode);
    }

    @Override
    public PjiEpisode getById(Long id) {
        return episodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found"));
    }

    @Override
    public void delete(Long id) {
        if (!episodeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Episode not found");
        }
        episodeRepository.deleteById(id);
    }

    @Override
    public PaginationResultDTO getAll(Specification<PjiEpisode> spec, Pageable pageable) {
        Page<PjiEpisode> page = episodeRepository.findAll(spec, pageable);
        return buildPaginationResult(page);
    }

    @Override
    public PaginationResultDTO getByPatient(Long patientId, Pageable pageable) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found");
        }
        Page<PjiEpisode> page = episodeRepository.findByPatientId(patientId, pageable);
        return buildPaginationResult(page);
    }

    private PjiEpisode buildFromDto(EpisodeRequestDTO data, PjiEpisode episode) {
        episode.setAdmissionDate(data.getAdmissionDate());
        episode.setDischargeDate(data.getDischargeDate());
        episode.setTreatmentDays(data.getTreatmentDays());
        episode.setReason(data.getReason());
        episode.setDepartment(data.getDepartment());
        episode.setDirect(data.getDirect());
        episode.setDaysTreatment(data.getDaysTreatment());
        episode.setReferralSource(data.getReferralSource());
        episode.setResult(data.getResult());
        return episode;
    }

    private PaginationResultDTO buildPaginationResult(Page<PjiEpisode> page) {
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
}
