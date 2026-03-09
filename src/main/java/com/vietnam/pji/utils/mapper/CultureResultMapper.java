package com.vietnam.pji.utils.mapper;

import com.vietnam.pji.dto.request.CultureResultRequestDTO;
import com.vietnam.pji.model.medical.CultureResult;
import org.mapstruct.*;

@Mapper(config = DefaultConfigMapper.class)
public interface CultureResultMapper extends EntityMapper<CultureResultRequestDTO, CultureResult> {

    @Override
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "episode", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    CultureResult toEntity(CultureResultRequestDTO dto);

    @Override
    @Named("update")
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "episode", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void update(CultureResultRequestDTO dto, @MappingTarget CultureResult entity);
}
