package com.vietnam.pji.utils.mapper;

import com.vietnam.pji.dto.request.ClinicalRecordRequestDTO;
import com.vietnam.pji.model.medical.ClinicalRecord;
import org.mapstruct.*;

@Mapper(config = DefaultConfigMapper.class)
public interface ClinicalRecordMapper extends EntityMapper<ClinicalRecordRequestDTO, ClinicalRecord> {

    @Override
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "episode", ignore = true)
    ClinicalRecord toEntity(ClinicalRecordRequestDTO dto);

    @Override
    @Named("update")
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "episode", ignore = true)
    void update(ClinicalRecordRequestDTO dto, @MappingTarget ClinicalRecord entity);
}
