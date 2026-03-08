package com.vietnam.pji.controller.auth;

import com.turkraft.springfilter.boot.Filter;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.dto.response.ResponseData;
import com.vietnam.pji.model.auth.Role;
import com.vietnam.pji.repository.RoleRepository;
import com.vietnam.pji.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final RoleRepository roleRepository;

    @PostMapping("/add-role")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Role> create(@RequestBody Role data) {
        if (roleRepository.existsByName(data.getName())) {
            throw new IllegalArgumentException("Dữ liệu bị trùng lặp");
        }
        return new ResponseData<>(HttpStatus.CREATED.value(), "Role created successfully", roleService.create(data));
    }

    @PutMapping("/update-role")
    public ResponseData<Void> update(@RequestBody Role data) {
        roleService.update(data);
        return new ResponseData<>(HttpStatus.OK.value(), "Role updated successfully");
    }

    @DeleteMapping("/delete-role/{id}")
    public ResponseData<Void> delete(@PathVariable("id") long id) {
        roleService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Role deleted successfully");
    }

    @GetMapping("/role/{id}")
    public ResponseData<Role> handleFetchSingle(@PathVariable("id") long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch role successfully", roleService.fetchById(id));
    }

    @GetMapping("/roles")
    public ResponseData<PaginationResultDTO> handleFetchAllRole(
            @Filter Specification<Role> spec, Pageable pageable) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch roles successfully", roleService.fetchAll(spec, pageable));
    }
}
