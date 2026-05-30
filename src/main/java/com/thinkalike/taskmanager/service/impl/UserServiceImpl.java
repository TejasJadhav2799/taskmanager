package com.thinkalike.taskmanager.service.impl;

import com.thinkalike.taskmanager.dto.UserRequest;
import com.thinkalike.taskmanager.dto.UserResponse;
import com.thinkalike.taskmanager.exception.ResourceNotFoundException;
import com.thinkalike.taskmanager.model.User;
import com.thinkalike.taskmanager.repository.UserRepository;
import com.thinkalike.taskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
// @RequiredArgsConstructor is Lombok — generates a constructor
// for all final fields, which Spring uses for dependency injection
// this replaces @Autowired — constructor injection is best practice
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(UserRequest request) {
        // RULE 1: email must be unique
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                    "User already exists with email: " + request.getEmail()
            );
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.MEMBER) // default role is MEMBER, not ADMIN
                .build();

        // save to database — repository handles the INSERT
        User savedUser = userRepository.save(user);

        // convert entity to DTO and return — never return the entity directly
        return UserResponse.from(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    // readOnly = true tells Hibernate this is a read operation
    // Hibernate skips dirty checking — better performance for queries
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id
                ));
        return UserResponse.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + email
                ));
        return UserResponse.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::from)
                .toList();
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id
                ));

        // only update fields that are allowed to change
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        User updatedUser = userRepository.save(user);
        return UserResponse.from(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "User not found with id: " + id
            );
        }
        userRepository.deleteById(id);
    }

}
