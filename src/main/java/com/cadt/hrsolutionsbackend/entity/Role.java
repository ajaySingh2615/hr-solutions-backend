package com.cadt.hrsolutionsbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Use String instead of Enum here for database flexibility.
    // We will validate the strings in the Service layer.
    @Column(nullable = false, unique = true)
    private String name;

    // Examples: "ROLE_SUPER_ADMIN", "ROLE_HR_EXECUTIVE"
}
