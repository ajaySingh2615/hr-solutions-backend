package com.cadt.hrsolutionsbackend.service.impl;

import com.cadt.hrsolutionsbackend.entity.RefreshToken;
import com.cadt.hrsolutionsbackend.repository.RefreshTokenRepository;
import com.cadt.hrsolutionsbackend.repository.UserRepository;
import com.cadt.hrsolutionsbackend.service.RefreshTokenService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    // 7 Days in Milliseconds
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public RefreshToken createRefreshToken(String username) {
        // 1. Check existing token (The "1 Session Rule")
        // Note: In a real "2 Session" scenario, we would count the list size here.
        refreshTokenRepository.findByUser(userRepository.findByUsernameOrEmail(username, username).get())
                .ifPresent(refreshTokenRepository::delete);

        // 2. Create New
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findByUsernameOrEmail(username, username).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_VALIDITY));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Override
    public void deleteByUserId(Long userId) {
        // We will implement this for the Logout API later
        // userRepository.findById(userId).ifPresent(user -> refreshTokenRepository.deleteByUser(user));
    }
}
