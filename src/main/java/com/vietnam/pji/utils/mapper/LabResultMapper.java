package com.vietnam.pji.utils.mapper;

import com.vietnam.pji.dto.request.LabResultRequestDTO;
import com.vietnam.pji.model.medical.LabResult;
import org.mapstruct.*;

@Mapper(config = DefaultConfigMapper.class)
public interface LabResultMapper extends EntityMapper<LabResultRequestDTO, LabResult> {

    @Override
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "episode", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    LabResult toEntity(LabResultRequestDTO dto);

    @Override
    @Named("update")
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "episode", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void update(LabResultRequestDTO dto, @MappingTarget LabResult entity);
}
