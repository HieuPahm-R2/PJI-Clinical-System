package com.vietnam.pji.utils.mapper;

import com.vietnam.pji.dto.request.UserRequestDTO;
import com.vietnam.pji.dto.response.UserDetailResponse;
import com.vietnam.pji.model.auth.User;
import org.mapstruct.*;

@Mapper(config = DefaultConfigMapper.class)
public interface UserMapper extends EntityMapper<UserRequestDTO, User> {

    @Override
    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "role", ignore = true)
    User toEntity(UserRequestDTO dto);

    @Override
    @Named("update")
    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "role", ignore = true)
    void update(UserRequestDTO dto, @MappingTarget User user);

    UserDetailResponse toUserDetailResponse(User user);
}
