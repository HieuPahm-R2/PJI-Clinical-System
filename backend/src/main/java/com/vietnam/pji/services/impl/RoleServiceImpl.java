package com.vietnam.pji.services.impl;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.model.auth.Permission;
import com.vietnam.pji.model.auth.Role;
import com.vietnam.pji.repository.PermissionRepository;
import com.vietnam.pji.repository.RoleRepository;
import com.vietnam.pji.services.RedisService;
import com.vietnam.pji.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RedisService redisService;


    @Override
    public Role create(Role data) {
        List<Long> pers = data.getPermissions().stream().map(item -> item.getId())
                .collect(Collectors.toList());
        if (pers.isEmpty()) {
            throw new IllegalArgumentException("Permission not be blank! Please check again!");
        }
        List<Permission> allPers = this.permissionRepository.findByIdIn(pers);
        data.setPermissions(allPers);
        Role saved = this.roleRepository.save(data);
        redisService.evictAllUserPermissions();
        return saved;
    }

    @Override
    public Role update(Role data)  {
        if (this.roleRepository.findById(data.getId()).isEmpty()) {
            throw new NoSuchElementException("Not found, check again?");
        } else {
            Optional<Role> rOptional = this.roleRepository.findById(data.getId());
            if (rOptional.isPresent()) {
                List<Long> allItems = data.getPermissions().stream().map(item -> item.getId())
                        .collect(Collectors.toList());
                List<Permission> allPermissions = this.permissionRepository.findByIdIn(allItems);
                data.setPermissions(allPermissions);
                data.setCreatedBy(rOptional.get().getCreatedBy());
                data.setCreatedAt(rOptional.get().getCreatedAt());
                Role saved = this.roleRepository.save(data);
                redisService.evictAllUserPermissions();
                return saved;
            }
            return null;
        }
    }

    @Override
    public Role fetchById(Long id)  {
        Optional<Role> check = this.roleRepository.findById(id);
        if (check.isEmpty()) {
            throw new NoSuchElementException("Data not found!");
        }
        return check.get();
    }

    @Override
    public void delete(Long id) {
        if (this.roleRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("Không tìm thấy dữ liệu! (Có thể do ID không hợp lệ)");
        }
        this.roleRepository.deleteById(id);
        redisService.evictAllUserPermissions();
    }

    @Override
    public PaginationResultDTO fetchAll(Specification<Role> spec, Pageable pageable) {
        Page<Role> page = this.roleRepository.findAll(spec, pageable);

        PaginationResultDTO rs = new PaginationResultDTO();
        PaginationResultDTO.Meta mt = new PaginationResultDTO.Meta();

        mt.setPage(page.getNumber() + 1);
        mt.setPageSize(page.getSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(page.getContent());
        return rs;
    }
}
