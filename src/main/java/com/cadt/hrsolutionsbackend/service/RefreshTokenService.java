package com.cadt.hrsolutionsbackend.service;

import com.cadt.hrsolutionsbackend.entity.RefreshToken;

public interface RefreshTokenService {

    RefreshToken createRefreshToken(String username);

    RefreshToken verifyExpiration(RefreshToken token);

    // We might need this later for "Logout"
    void deleteByUserId(Long userId);
}