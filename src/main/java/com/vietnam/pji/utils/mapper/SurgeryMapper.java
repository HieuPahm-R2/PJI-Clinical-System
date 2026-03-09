package com.vietnam.pji.utils.mapper;

import com.vietnam.pji.dto.request.SurgeryRequestDTO;
import com.vietnam.pji.model.medical.Surgery;
import org.mapstruct.*;

@Mapper(config = DefaultConfigMapper.class)
public interface SurgeryMapper extends EntityMapper<SurgeryRequestDTO, Surgery> {

    @Override
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "episode", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Surgery toEntity(SurgeryRequestDTO dto);

    @Override
    @Named("update")
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "episode", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void update(SurgeryRequestDTO dto, @MappingTarget Surgery entity);
}
