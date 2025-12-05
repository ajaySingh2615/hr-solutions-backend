package com.cadt.hrsolutionsbackend.service.impl;

import com.cadt.hrsolutionsbackend.entity.RefreshToken;
import com.cadt.hrsolutionsbackend.entity.User;
import com.cadt.hrsolutionsbackend.repository.RefreshTokenRepository;
import com.cadt.hrsolutionsbackend.repository.UserRepository;
import com.cadt.hrsolutionsbackend.service.RefreshTokenService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    // 7 Days in Milliseconds
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000;
    private static final int MAX_SESSIONS = 2;  // the Limit

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional  // Ensures the delete and save happen together
    public RefreshToken createRefreshToken(String username) {

        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 1. Get all active sessions for this user
        List<RefreshToken> activeSessions = refreshTokenRepository.findByUser(user);

        // 2. Check the Limit
        if (activeSessions.size() >= MAX_SESSIONS) {
            // Logic: Find the session with the OLDEST expiry date (or ID) and kill it.
            // this kicks out the device that logged in the longest ago.
            RefreshToken oldestSession = activeSessions.stream()
                    .min(Comparator.comparing(RefreshToken::getId))  // Assuming ID implies insertion order
                    .orElseThrow();

            refreshTokenRepository.delete(oldestSession);
        }

        // 3. Create the New Token
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
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
    @Transactional
    public void deleteByUserId(Long userId) {
        userRepository.findById(userId).ifPresent(refreshTokenRepository::deleteByUser);
    }
}
