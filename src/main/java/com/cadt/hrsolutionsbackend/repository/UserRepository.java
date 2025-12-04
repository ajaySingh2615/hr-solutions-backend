package com.cadt.hrsolutionsbackend.repository;

import com.cadt.hrsolutionsbackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 1. For login (find by email)
    Optional<User> findByEmail(String email);

    // 2. For Flexible login (username or email)
    // SQL: select * from users where username = ? or email = ?
    Optional<User> findByUsernameOrEmail(String username, String email);

    // 3. For Registration Validation (Check duplicates)
    // SQL: select count(*) > 0 from users where username = ?
    Boolean existsByUsername(String username);

    // 4. SQL: select count(*) > 0 from users where email = ?
    Boolean existsByEmail(String email);
}
