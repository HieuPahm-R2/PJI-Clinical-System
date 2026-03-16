package com.vietnam.pji.services;

import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.model.auth.Role;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface RoleService {
    Role create(Role data);

    Role update(Role data);

    Role fetchById(Long id);

    void delete(Long id);

    PaginationResultDTO fetchAll(Specification<Role> spec, Pageable pageable);
}
