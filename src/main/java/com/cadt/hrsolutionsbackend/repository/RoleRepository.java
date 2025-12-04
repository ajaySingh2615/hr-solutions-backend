package com.cadt.hrsolutionsbackend.repository;

import com.cadt.hrsolutionsbackend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    // Custom Query: SELECT * FROM roles WHERE name = ?
    // We use Optional because the role might not exist in the DB yet.
    Optional<Role> findByName(String name);
}
