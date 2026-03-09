package com.vietnam.pji.controller.auth;

import com.vietnam.pji.dto.request.LoginDTO;
import com.vietnam.pji.dto.response.ResLoginDTO;
import com.vietnam.pji.dto.response.ResponseData;
import com.vietnam.pji.exception.InvalidDataException;
import com.vietnam.pji.exception.ResourceNotFoundException;
import com.vietnam.pji.model.auth.User;
import com.vietnam.pji.services.RedisService;
import com.vietnam.pji.services.UserService;
import com.vietnam.pji.utils.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}")
public class AuthController {

    @Value("${secure.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpire;

    private final UserService userService;
    private final RedisService RedisService;
    private final AuthenticationManager authenticationManager;
    private final SecurityUtils securityUtils;

    @PostMapping("/auth/login")
    public ResponseEntity<ResponseData<ResLoginDTO>> login(@Valid @RequestBody LoginDTO loginData) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginData.getUsername(), loginData.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User realUser = this.userService.handleGetUserByUsername(loginData.getUsername());
        if (realUser != null) {
            ResLoginDTO.UserData userLog = new ResLoginDTO.UserData(
                    realUser.getId(),
                    realUser.getFullName(),
                    realUser.getEmail(),
                    realUser.getRole());
            resLoginDTO.setUser(userLog);
        }
        String access_token = this.securityUtils.generateAccessToken(authentication.getName(), resLoginDTO);
        resLoginDTO.setAccessToken(access_token);
        String refresh_token = this.securityUtils.generateRefreshToken(loginData.getUsername(), resLoginDTO);
        this.RedisService.saveRefreshToken(loginData.getUsername(), refresh_token, refreshTokenExpire);
        this.userService.saveRefreshToken(refresh_token, loginData.getUsername());

        ResponseCookie resCookies = ResponseCookie
                .from("refresh-token", refresh_token)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenExpire)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(new ResponseData<>(HttpStatus.OK.value(), "Login successful", resLoginDTO));
    }

    @GetMapping("/auth/account")
    public ResponseData<ResLoginDTO.GetAccountUser> getAccount() {
        String emailLogin = SecurityUtils.getCurrentUserLogin().isPresent()
                ? SecurityUtils.getCurrentUserLogin().get()
                : "";
        User userCreated = this.userService.handleGetUserByUsername(emailLogin);
        ResLoginDTO.UserData userData = new ResLoginDTO.UserData();
        ResLoginDTO.GetAccountUser info = new ResLoginDTO.GetAccountUser();
        if (userCreated != null) {
            userData.setId(userCreated.getId());
            userData.setEmail(userCreated.getEmail());
            userData.setName(userCreated.getFullName());
            userData.setRole(userCreated.getRole());
            info.setUser(userData);
        }
        return new ResponseData<>(HttpStatus.OK.value(), "Fetch account successfully", info);
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<ResponseData<ResLoginDTO>> getRefreshToken(
            @CookieValue(name = "refresh-token", defaultValue = "error") String refreshToken) throws Exception {
        if (refreshToken.equals("error")) {
            throw new ResourceNotFoundException("Refresh token not be attached in request");
        }
        Jwt correctToken;
        try {
            correctToken = this.securityUtils.confirmValidRefreshToken(refreshToken);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseData<>(HttpStatus.BAD_REQUEST.value(), "Invalid or expired refresh token"));
        }
        String email = correctToken.getSubject();

        if (!this.RedisService.validateRefreshToken(email, refreshToken)) {
            User currentUser = this.userService.fetchWithTokenAndEmail(refreshToken, email);
            if (currentUser == null) {
                throw new Exception("Invalid refresh token");
            }
        }

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User realUser = this.userService.handleGetUserByUsername(email);
        if (realUser != null) {
            ResLoginDTO.UserData userLog = new ResLoginDTO.UserData(
                    realUser.getId(),
                    realUser.getEmail(),
                    realUser.getFullName(),
                    realUser.getRole());
            resLoginDTO.setUser(userLog);
        }
        String access_token = this.securityUtils.generateAccessToken(email, resLoginDTO);
        resLoginDTO.setAccessToken(access_token);
        String refresh_token = this.securityUtils.generateRefreshToken(email, resLoginDTO);
        this.RedisService.saveRefreshToken(email, refresh_token, refreshTokenExpire);
        this.userService.saveRefreshToken(refresh_token, email);

        ResponseCookie resCookies = ResponseCookie
                .from("refresh-token", refresh_token)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenExpire)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(new ResponseData<>(HttpStatus.OK.value(), "Token refreshed successfully", resLoginDTO));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<ResponseData<Void>> logoutAccount() {
        String email = SecurityUtils.getCurrentUserLogin().isPresent()
                ? SecurityUtils.getCurrentUserLogin().get()
                : "";
        if (email.isEmpty()) {
            throw new InvalidDataException("Something wrong with access token");
        }
        this.RedisService.deleteRefreshToken(email);
        this.userService.saveRefreshToken(null, email);

        ResponseCookie removeCookies = ResponseCookie
                .from("refresh-token", null)
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, removeCookies.toString())
                .body(new ResponseData<>(HttpStatus.OK.value(), "Logout successful"));
    }
}
