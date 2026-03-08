package com.vietnam.pji.services;

public interface RedisService {
    /**
     * save refresh token vào Redis với TTL
     *
     * @param email                   email người dùng
     * @param refreshToken            token cần lưu
     * @param expirationTimeInSeconds thời gian hết hạn
     */
    void saveRefreshToken(String email, String refreshToken, long expirationTimeInSeconds);

    /**
     * Lấy refresh token từ Redis
     *
     * @param email email user
     * @return refresh token hoặc null nếu không tồn tại
     */
    String getRefreshToken(String email);

    /**
     * Xóa refresh token khỏi Redis (logout)
     *
     * @param email email người dùng
     */
    void deleteRefreshToken(String email);

    /**
     * Kiểm tra refresh token có tồn tại trong Redis
     *
     * @param email        email người dùng
     * @param refreshToken token cần kiểm tra
     * @return true nếu token hợp lệ
     */
    boolean validateRefreshToken(String email, String refreshToken);

    /**
     * Thêm access token vào blacklist khi logout
     *
     * @param accessToken             token cần blacklist
     * @param expirationTimeInSeconds thời gian hết hạn
     */
    void blacklistAccessToken(String accessToken, long expirationTimeInSeconds);

    /**
     * Kiểm tra access token có trong blacklist
     *
     * @param accessToken token cần kiểm tra
     * @return true nếu token bị blacklist
     */
    boolean isAccessTokenBlacklisted(String accessToken);
}
