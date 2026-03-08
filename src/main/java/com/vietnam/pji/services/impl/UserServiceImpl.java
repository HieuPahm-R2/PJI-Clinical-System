package com.vietnam.pji.services.impl;

import com.vietnam.pji.dto.request.UserRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.dto.response.UserDetailResponse;
import com.vietnam.pji.exception.InvalidDataException;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.auth.Role;
import com.vietnam.pji.model.auth.User;
import com.vietnam.pji.repository.RoleRepository;
import com.vietnam.pji.repository.UserRepository;
import com.vietnam.pji.services.UserService;
import com.vietnam.pji.utils.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDetailResponse create(UserRequestDTO data) {
        if (userRepository.existsByEmail(data.getEmail())) {
            throw new InvalidDataException("User with this email already exists.");
        }
        User user = userMapper.toUser(data);
        if (data.getRoleId() != null) {
            Role role = roleRepository.findById(data.getRoleId()).orElse(null);
            user.setRole(role);
        }
        user.setPassword(passwordEncoder.encode(data.getPassword()));
        return userMapper.toUserDetailResponse(userRepository.save(user));
    }

    @Override
    public UserDetailResponse update(UserRequestDTO data) {
        User user = userRepository.findById(data.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userMapper.updateUserFromDto(data, user);
        if (data.getRoleId() != null) {
            Role role = roleRepository.findById(data.getRoleId()).orElse(null);
            user.setRole(role);
        }
        return userMapper.toUserDetailResponse(userRepository.save(user));
    }

    @Override
    public UserDetailResponse getInfo(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toUserDetailResponse(user);
    }

    @Override
    public PaginationResultDTO getAll(Specification<User> spec, Pageable pageable) {
        Page<User> page = userRepository.findAll(spec, pageable);
        PaginationResultDTO.Meta mt = new PaginationResultDTO.Meta();
        mt.setPage(page.getNumber() + 1);
        mt.setPageSize(page.getSize());
        mt.setPages(page.getTotalPages());
        mt.setTotal(page.getTotalElements());

        List<UserDetailResponse> users = page.getContent().stream()
                .map(userMapper::toUserDetailResponse)
                .collect(Collectors.toList());

        PaginationResultDTO res = new PaginationResultDTO();
        res.setMeta(mt);
        res.setResult(users);
        return res;
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public User handleGetUserByUsername(String username) {
        return userRepository.findByEmail(username);
    }

    @Override
    public User fetchWithTokenAndEmail(String token, String email) {
        return userRepository.findByRefreshTokenAndEmail(token, email);
    }

    @Override
    public void saveRefreshToken(String token, String email) {
        User user = handleGetUserByUsername(email);
        if (user != null) {
            user.setRefreshToken(token);
            userRepository.save(user);
        }
    }
}
