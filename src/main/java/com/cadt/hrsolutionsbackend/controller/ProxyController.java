package com.cadt.hrsolutionsbackend.controller;

import com.cadt.hrsolutionsbackend.entity.Role;
import com.cadt.hrsolutionsbackend.entity.User;
import com.cadt.hrsolutionsbackend.payload.ProxyDto;
import com.cadt.hrsolutionsbackend.repository.RoleRepository;
import com.cadt.hrsolutionsbackend.repository.UserRepository;
import com.cadt.hrsolutionsbackend.utils.PasswordGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/proxy")
public class ProxyController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public ProxyController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Only HR and Agency Admin can do this
    @PreAuthorize("hasRole('HR_EXECUTIVE') or hasRole('AGENCY_ADMIN')")
    @PostMapping("/create-candidate")
    public ResponseEntity<String> createCandidateByProxy(@RequestBody ProxyDto proxyDto) {

        if (userRepository.existsByEmail(proxyDto.getEmail())) {
            return new ResponseEntity<>("candidate email already exists!", HttpStatus.BAD_REQUEST);
        }

        // 1. Generate Temp Password
        String tempPassword = PasswordGenerator.generateRandomPassword(8);
        System.out.println("TEMP PASSWORD FOR " + proxyDto.getEmail() + ": " + tempPassword);
        // TODO: Send this via Email Service later

        // 2. Create User
        User user = new User();
        user.setName(proxyDto.getName());
        user.setEmail(proxyDto.getEmail());
        user.setUsername(proxyDto.getEmail());  // Use email as username default
        user.setPassword(passwordEncoder.encode(tempPassword));  // Encrypt it


        // 3. Assign Role CANDIDATE
        Role role = roleRepository.findByName("ROLE_CANDIDATE").get();
        user.setRoles(Collections.singleton(role));

        userRepository.save(user);

        return new ResponseEntity<>("Candidate created. Temp password generated.", HttpStatus.CREATED);
    }
}
