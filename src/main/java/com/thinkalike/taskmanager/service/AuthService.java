package com.thinkalike.taskmanager.service;

import com.thinkalike.taskmanager.dto.AuthResponse;
import com.thinkalike.taskmanager.dto.LoginRequest;
import com.thinkalike.taskmanager.dto.RegisterRequest;
import com.thinkalike.taskmanager.exception.ResourceNotFoundException;
import com.thinkalike.taskmanager.model.User;
import com.thinkalike.taskmanager.repository.UserRepository;
import com.thinkalike.taskmanager.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        // check email not already taken
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already registered: " + request.getEmail()
            );
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                // BCrypt hashes the password — never store plain text
                // BCrypt output looks like:
                // $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.MEMBER)
                .build();

        userRepository.save(user);

        // generate JWT token immediately after registration
        // user is logged in right after signing up
        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No account found with email: " + request.getEmail()
                ));

        // verify password against BCrypt hash
        // passwordEncoder.matches() hashes the input and compares
        // you never decrypt BCrypt — you re-hash and compare
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        // generate and return JWT token
        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole().name())
                .build();
    }
}