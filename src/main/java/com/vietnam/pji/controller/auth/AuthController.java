package com.vietnam.pji.controller.auth;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}")
public class AuthController {
    @Value("${group29.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpire;

    private final IUserService userService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtils securityUtils;

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody LoginDTO loginData) {
        // transfer input username/password
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginData.getUsername(), loginData.getPassword());
        // xác thực người dùng ==> cần có loadUserByUserName
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User realUser = this.userService.handleGetUserByUsername(loginData.getUsername());
        if (realUser != null) {
            ResLoginDTO.UserData userLog = new ResLoginDTO.UserData(
                    realUser.getId(),
                    realUser.getUsername(),
                    realUser.getEmail(),
                    realUser.getRole());
            resLoginDTO.setUser(userLog);
        }
        // generate access token
        String access_token = this.securityUtils.generateAccessToken(authentication.getName(), resLoginDTO);
        resLoginDTO.setAccessToken(access_token);
        // gen refresh token
        String refresh_token = this.securityUtils.generateRefreshToken(loginData.getUsername(), resLoginDTO);
        this.userService.saveRefreshToken(access_token, loginData.getUsername());
        // setup cookies
        ResponseCookie resCookies = ResponseCookie
                .from("refresh-token", refresh_token)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenExpire)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(resLoginDTO);
    }

    @PostMapping("/auth/register")
    @MessageApi("Register account")
    public ResponseEntity<RegisterDTO> registerAccount(@Valid @RequestBody User dataUser) throws BadActionException {
        // User accUser = this.userService.handleCreateUser(dataUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.create(dataUser));
    }

    @GetMapping("/auth/account")
    public ResponseEntity<ResLoginDTO.GetAccountUser> getAccount() {
        try{
            String emailLogin = SecurityUtils.getCurrentUserLogin().isPresent()
                    ? SecurityUtils.getCurrentUserLogin().get()
                    : "";
            User userCreated = this.userService.handleGetUserByUsername(emailLogin);
            ResLoginDTO.UserData userData = new ResLoginDTO.UserData();
            ResLoginDTO.GetAccountUser info = new ResLoginDTO.GetAccountUser();
            if (userCreated != null) {
                userData.setId(userCreated.getId());
                userData.setEmail(userCreated.getEmail());
                userData.setName(userCreated.getUsername());
                userData.setRole(userCreated.getRole());
                info.setUser(userData);
            }
            return ResponseEntity.ok().body(info);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }

    }

    @GetMapping("/auth/refresh")
    @MessageApi("Renew token action")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh-token", defaultValue = "error") String refreshToken)
            throws BadActionException {
        if (refreshToken.equals("error")) {
            throw new BadActionException("Refresh token not be attached in request");
        }
        Jwt correctToken;
        try {
            // running check valid
            correctToken = this.securityUtils.confirmValidRefreshToken(refreshToken);
        } catch (Exception ex) {
            // Nếu token hết hạn hoặc không hợp lệ, trả về lỗi 400
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        }
        String email = correctToken.getSubject();
        User currentUser = this.userService.fetchWithTokenAndEmail(refreshToken, email);
        if (currentUser == null) {
            throw new BadActionException("Invalid refresh token");
        }

        ResLoginDTO resLoginDTO = new ResLoginDTO();
        User realUser = this.userService.handleGetUserByUsername(email);
        if (realUser != null) {
            ResLoginDTO.UserData userLog = new ResLoginDTO.UserData(
                    realUser.getId(),
                    realUser.getEmail(),
                    realUser.getUsername(),
                    realUser.getRole());
            resLoginDTO.setUser(userLog);
        }
        // generate access token
        String access_token = this.securityUtils.generateAccessToken(email, resLoginDTO);
        resLoginDTO.setAccessToken(access_token);
        // gen refresh token
        String refresh_token = this.securityUtils.generateRefreshToken(email, resLoginDTO);
        this.userService.saveRefreshToken(access_token, email);
        // setup cookies
        ResponseCookie resCookies = ResponseCookie
                .from("refresh-token", refresh_token)
                .httpOnly(true)
                .path("/")
                .maxAge(refreshTokenExpire)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(resLoginDTO);
    }

    @PostMapping("/auth/logout")
    @MessageApi("sign out action")
    public ResponseEntity<Void> LogoutAccount() throws BadActionException {
        String email = SecurityUtils.getCurrentUserLogin().isPresent() ? SecurityUtils.getCurrentUserLogin().get() : "";
        if (email.equals("")) {
            throw new BadActionException("Something wrong with access token");
        }
        // set null value
        this.userService.saveRefreshToken(null, email);
        ResponseCookie removeCookies = ResponseCookie
                .from("refresh-token", null)
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, removeCookies.toString())
                .body(null);
    }
}
