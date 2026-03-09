package com.vietnam.pji.utils.mapper;

import com.vietnam.pji.dto.request.EpisodeRequestDTO;
import com.vietnam.pji.model.medical.PjiEpisode;
import org.mapstruct.*;

@Mapper(config = DefaultConfigMapper.class)
public interface EpisodeMapper extends EntityMapper<EpisodeRequestDTO, PjiEpisode> {

    @Override
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "patient", ignore = true)
    PjiEpisode toEntity(EpisodeRequestDTO dto);

    @Override
    @Named("update")
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "patient", ignore = true)
    void update(EpisodeRequestDTO dto, @MappingTarget PjiEpisode entity);
}
