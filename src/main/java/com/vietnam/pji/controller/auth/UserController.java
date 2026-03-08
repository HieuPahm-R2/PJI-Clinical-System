package com.vietnam.pji.controller.auth;

import com.turkraft.springfilter.boot.Filter;
import com.vietnam.pji.dto.request.UserRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.dto.response.ResponseData;
import com.vietnam.pji.dto.response.UserDetailResponse;
import com.vietnam.pji.model.auth.User;
import com.vietnam.pji.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseData<UserDetailResponse> createUser(@Valid @RequestBody UserRequestDTO request) {
        return new ResponseData<>(HttpStatus.CREATED.value(), "User created successfully", userService.create(request));
    }

    @PutMapping("/update-user")
    public ResponseData<Void> updateUser(@Valid @RequestBody UserRequestDTO request) {
        userService.update(request);
        return new ResponseData<>(HttpStatus.OK.value(), "User updated successfully");
    }

    @GetMapping("/user/{id}")
    public ResponseData<UserDetailResponse> getInfo(@PathVariable long id) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch user successfully", userService.getInfo(id));
    }

    @DeleteMapping("/delete-user/{id}")
    public ResponseData<Void> deleteUser(@PathVariable long id) {
        userService.delete(id);
        return new ResponseData<>(HttpStatus.OK.value(), "User deleted successfully");
    }

    @GetMapping("/users")
    public ResponseData<PaginationResultDTO> getAllUsersInfo(
            @Filter Specification<User> spec, Pageable pageable) {
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch users successfully", userService.getAll(spec, pageable));
    }
}
