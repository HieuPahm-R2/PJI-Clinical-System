package com.vietnam.pji.services;

import com.vietnam.pji.dto.request.UserRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.dto.response.UserDetailResponse;
import com.vietnam.pji.model.auth.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserService {

    UserDetailResponse create(UserRequestDTO data);

    UserDetailResponse update(UserRequestDTO data);

    UserDetailResponse getInfo(Long id);

    PaginationResultDTO getAll(Specification<User> spec, Pageable pageable);

    void delete(Long id);

    // Auth helpers
    User handleGetUserByUsername(String username);

    User fetchWithTokenAndEmail(String token, String email);

    void saveRefreshToken(String token, String email);
}
