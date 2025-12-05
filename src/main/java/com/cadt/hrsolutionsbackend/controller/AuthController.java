package com.cadt.hrsolutionsbackend.controller;

import com.cadt.hrsolutionsbackend.entity.RefreshToken;
import com.cadt.hrsolutionsbackend.entity.Role;
import com.cadt.hrsolutionsbackend.entity.User;
import com.cadt.hrsolutionsbackend.payload.LoginDto;
import com.cadt.hrsolutionsbackend.payload.RegisterDto;
import com.cadt.hrsolutionsbackend.repository.RefreshTokenRepository;
import com.cadt.hrsolutionsbackend.repository.RoleRepository;
import com.cadt.hrsolutionsbackend.repository.UserRepository;
import com.cadt.hrsolutionsbackend.security.JwtTokenProvider;
import com.cadt.hrsolutionsbackend.service.RefreshTokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository, RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, RefreshTokenRepository refreshTokenRepository, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenService = refreshTokenService;
    }

    // 1. Login API
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 1. Generate Access Token (Short Lived - 15 mins)
        String accessToken = jwtTokenProvider.generateToken(authentication);

        // 2. Generate Refresh Token (Long Lived - 7 days)
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginDto.getUsernameOrEmail());

        // 3. Return Both
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken.getToken());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 2. Register API
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
        // Check if username/email exists
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>("username is already taken!", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(registerDto.getEmail())) {
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }

        // Create User
        User user = new User();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        user.setUsername(registerDto.getUsername());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        // Assign Default Role (ROLE_CANDIDATE or ROLE_HR_EXECUTIVE)
        // Note: Ensure roles exist in DB first!
        Role roles = roleRepository.findByName("ROLE_HR_EXECUTIVE").get();
        user.setRoles(Collections.singleton(roles));

        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.OK);
    }
}
