package com.vietnam.pji.utils.mapper;

import com.vietnam.pji.dto.request.MedicalHistoryRequestDTO;
import com.vietnam.pji.model.medical.MedicalHistory;
import org.mapstruct.*;

@Mapper(config = DefaultConfigMapper.class)
public interface MedicalHistoryMapper extends EntityMapper<MedicalHistoryRequestDTO, MedicalHistory> {

    @Override
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "episode", ignore = true)
    MedicalHistory toEntity(MedicalHistoryRequestDTO dto);

    @Override
    @Named("update")
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "episode", ignore = true)
    void update(MedicalHistoryRequestDTO dto, @MappingTarget MedicalHistory entity);
}
