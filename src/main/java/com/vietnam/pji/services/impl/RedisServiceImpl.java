package com.vietnam.pji.services.impl;

import com.vietnam.pji.services.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_TOKEN_KEY_PREFIX = "refresh_token:";
    private static final String ACCESS_TOKEN_BLACKLIST_KEY_PREFIX = "access_token_blacklist:";

    @Override
    public void saveRefreshToken(String email, String refreshToken, long expirationTimeInSeconds) {
        String key = REFRESH_TOKEN_KEY_PREFIX + email;
        redisTemplate.opsForValue().set(key, refreshToken, expirationTimeInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public String getRefreshToken(String email) {
        String key = REFRESH_TOKEN_KEY_PREFIX + email;
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void deleteRefreshToken(String email) {
        String key = REFRESH_TOKEN_KEY_PREFIX + email;
        redisTemplate.delete(key);
    }

    @Override
    public boolean validateRefreshToken(String email, String refreshToken) {
        String storedToken = getRefreshToken(email);
        return Objects.equals(storedToken, refreshToken);
    }

    @Override
    public void blacklistAccessToken(String accessToken, long expirationTimeInSeconds) {
        String key = ACCESS_TOKEN_BLACKLIST_KEY_PREFIX + accessToken;
        redisTemplate.opsForValue().set(key, "blacklisted", expirationTimeInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean isAccessTokenBlacklisted(String accessToken) {
        String key = ACCESS_TOKEN_BLACKLIST_KEY_PREFIX + accessToken;
        return redisTemplate.hasKey(key);
    }
}
