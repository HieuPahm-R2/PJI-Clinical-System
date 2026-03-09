package com.vietnam.pji.utils.mapper;

import com.vietnam.pji.dto.request.PatientRequestDTO;
import com.vietnam.pji.model.medical.Patient;
import org.mapstruct.*;

@Mapper(config = DefaultConfigMapper.class)
public interface PatientMapper extends EntityMapper<PatientRequestDTO, Patient> {

    @Override
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Patient toEntity(PatientRequestDTO dto);

    @Override
    @Named("update")
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void update(PatientRequestDTO dto, @MappingTarget Patient entity);
}
