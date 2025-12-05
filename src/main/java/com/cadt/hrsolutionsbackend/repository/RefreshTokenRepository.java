package com.cadt.hrsolutionsbackend.repository;

import com.cadt.hrsolutionsbackend.entity.RefreshToken;
import com.cadt.hrsolutionsbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    // To check existing sessions for a user
    Optional<RefreshToken> findByUser(User user);

    // Useful for "Delete this specific session"
    int deleteByUser(User user);
}
