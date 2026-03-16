package com.vietnam.pji.controller.auth;

import com.turkraft.springfilter.boot.Filter;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.dto.response.ResponseData;
import com.vietnam.pji.model.auth.Permission;
import com.vietnam.pji.services.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping("/add-permission")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<Permission> create(@RequestBody Permission data) {
        if (this.permissionService.alreadyExistPermission(data)) {
            throw new IllegalArgumentException("Đã tồn tại, hãy thử lại!");
        }
        return new ResponseData<>(HttpStatus.CREATED.value(), "Permission created successfully", permissionService.create(data));
    }

    @PutMapping("/update-permission")
    public ResponseData<Void> update(@RequestBody Permission data) {
        permissionService.update(data);
        return new ResponseData<>(HttpStatus.OK.value(), "Permission updated successfully");
    }

    @DeleteMapping("/delete-permission/{id}")
    public ResponseData<Void> handleDelete(@PathVariable("id") long id) {
        permissionService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "Permission deleted successfully");
    }

    @GetMapping("/permission/{id}")
    public ResponseData<Permission> getById(@PathVariable long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch permission successfully", permissionService.getById(id));
    }

    @GetMapping("/permissions")
    public ResponseData<PaginationResultDTO> handleFetchAllPermission(
            @Filter Specification<Permission> spec, Pageable pageable) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch permissions successfully", permissionService.fetchAll(spec, pageable));
    }
}
