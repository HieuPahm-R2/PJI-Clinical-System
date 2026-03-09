package com.vietnam.pji.utils.mapper;

import com.vietnam.pji.dto.request.SensitivityResultRequestDTO;
import com.vietnam.pji.model.medical.SensitivityResult;
import org.mapstruct.*;

@Mapper(config = DefaultConfigMapper.class)
public interface SensitivityResultMapper extends EntityMapper<SensitivityResultRequestDTO, SensitivityResult> {

    @Override
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "culture", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    SensitivityResult toEntity(SensitivityResultRequestDTO dto);

    @Override
    @Named("update")
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "culture", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void update(SensitivityResultRequestDTO dto, @MappingTarget SensitivityResult entity);
}
