package com.vietnam.pji.services;

import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.model.auth.Permission;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface PermissionService {
    Permission create(Permission data);

    Permission update(Permission data) ;

    Permission getById(Long id);

    void delete(Long id);

    PaginationResultDTO fetchAll(Specification<Permission> spec, Pageable pageable);

    boolean alreadyExistPermission(Permission data);

    boolean isEqualName(String s);
}
