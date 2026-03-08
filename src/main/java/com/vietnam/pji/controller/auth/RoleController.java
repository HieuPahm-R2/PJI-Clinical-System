package com.vietnam.pji.controller.auth;

import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.model.auth.Role;
import com.vietnam.pji.repository.RoleRepository;
import com.vietnam.pji.services.RoleService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

@RestController
@RequestMapping("${api.prefix}")
public class RoleController {
    private final RoleService roleService;
    private final RoleRepository roleRepository;

    public RoleController(RoleService roleService, RoleRepository roleRepository) {
        this.roleService = roleService;
        this.roleRepository = roleRepository;
    }

    @PostMapping("/add-role")
    public ResponseEntity<Role> create(@RequestBody Role data) {
        if (this.roleRepository.existsByName(data.getName())) {
            throw new IllegalArgumentException("Dữ liệu bị trùng lặp");
        }
        return ResponseEntity.ok().body(this.roleService.create(data));
    }

    @PutMapping("/update-role")
    public ResponseEntity<Role> update(@RequestBody Role data) {
        return ResponseEntity.ok().body(this.roleService.update(data));
    }

    @DeleteMapping("/delete-role/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") long id){
        this.roleService.delete(id);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/roles")
    public ResponseEntity<PaginationResultDTO> handleFetchAllRole(
            @Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok().body(this.roleService.fetchAll(spec, pageable));
    }

    @GetMapping("/role/{id}")
    public ResponseEntity<Role> handleFetchSingle(@PathVariable("id") long id) {
        return ResponseEntity.ok().body(this.roleService.fetchById(id));
    }
}
