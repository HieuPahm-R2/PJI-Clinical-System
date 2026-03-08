package com.vietnam.pji.controller.auth;

import com.vietnam.pji.dto.request.UserRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.dto.response.UserDetailResponse;
import com.vietnam.pji.model.auth.User;
import com.vietnam.pji.services.UserService;
import com.turkraft.springfilter.boot.Filter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}")
@Validated
@Slf4j
@Tag(name = "User Controller")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/add-user")
    public ResponseEntity<UserDetailResponse> createUser(@Valid @RequestBody UserRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @PutMapping("/update-user")
    public ResponseEntity<UserDetailResponse> updateUser(@Valid @RequestBody UserRequestDTO request) {
        return ResponseEntity.ok(userService.update(request));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserDetailResponse> getInfo(@PathVariable long id) {
        return ResponseEntity.ok(userService.getInfo(id));
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<PaginationResultDTO> getAllUsersInfo(
            @Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.ok(userService.getAll(spec, pageable));
    }
}
