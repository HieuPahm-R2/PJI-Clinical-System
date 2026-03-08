package com.vietnam.pji.services.impl;

import com.vietnam.pji.dto.request.PatientRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.exception.InvalidDataException;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.medical.Patient;
import com.vietnam.pji.repository.PatientRepository;
import com.vietnam.pji.services.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

    @Override
    public Patient create(PatientRequestDTO data) {
        if (data.getIdentityCard() != null && patientRepository.existsByIdentityCard(data.getIdentityCard())) {
            throw new InvalidDataException("Patient with this identity card already exists.");
        }
        Patient patient = buildFromDto(data, new Patient());
        return patientRepository.save(patient);
    }

    @Override
    public Patient update(Long id, PatientRequestDTO data) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        buildFromDto(data, patient);
        return patientRepository.save(patient);
    }

    @Override
    public Patient getById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
    }

    @Override
    public void delete(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Patient not found");
        }
        patientRepository.deleteById(id);
    }

    @Override
    public PaginationResultDTO getAll(Specification<Patient> spec, Pageable pageable) {
        Page<Patient> page = patientRepository.findAll(spec, pageable);
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

    private Patient buildFromDto(PatientRequestDTO data, Patient patient) {
        patient.setFullName(data.getFullName());
        patient.setDateOfBirth(data.getDateOfBirth());
        patient.setGender(data.getGender());
        patient.setIdentityCard(data.getIdentityCard());
        patient.setInsuranceNumber(data.getInsuranceNumber());
        patient.setInsuranceExpired(data.getInsuranceExpired());
        patient.setNationality(data.getNationality());
        patient.setEthnicity(data.getEthnicity());
        patient.setPhone(data.getPhone());
        patient.setCareer(data.getCareer());
        patient.setSubject(data.getSubject());
        patient.setAddress(data.getAddress());
        patient.setRelativeInfo(data.getRelativeInfo());
        return patient;
    }
}
