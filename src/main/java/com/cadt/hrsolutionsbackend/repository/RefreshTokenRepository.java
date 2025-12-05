package com.cadt.hrsolutionsbackend.repository;

import com.cadt.hrsolutionsbackend.entity.RefreshToken;
import com.cadt.hrsolutionsbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    // CHANGED: Return a List, because there might be 2 sessions
    List<RefreshToken> findByUser(User user);

    // Clean up all sessions for a user (e.g. "Logout All Devices")
    void deleteByUser(User user);
}
