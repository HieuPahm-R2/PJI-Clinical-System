package com.vietnam.pji.utils.mapper;

import com.vietnam.pji.dto.request.PatientRequestDTO;
import com.vietnam.pji.model.medical.Patient;
import org.mapstruct.*;

@Mapper(config = DefaultConfigMapper.class)
public interface PatientMapper extends EntityMapper<PatientRequestDTO, Patient> {


}
