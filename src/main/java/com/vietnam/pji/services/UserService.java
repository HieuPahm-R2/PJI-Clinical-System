package com.vietnam.pji.services;

import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.model.auth.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface UserService {
    public RegisterDTO create(User data) throws Exception;

    public UserDTO getInfo(Long id);

    public PaginationResultDTO getAll(Specification<User> spec, Pageable pageable);

    public void delete(Long id) throws Exception;

    public UserDTO update(User data) throws Exception;

    public User handleGetUserByUsername(String username);

    // Authenticate
    public User fetchWithTokenAndEmail(String token, String email);

    public void saveRefreshToken(String token, String email);
}
