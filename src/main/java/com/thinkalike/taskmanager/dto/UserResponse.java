package com.thinkalike.taskmanager.dto;

import com.thinkalike.taskmanager.model.User;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String email;
    private User.Role role;
    private LocalDateTime createdAt;

    // notice: NO password field — never return password in API response
    // even hashed passwords should not be exposed

    // static factory method — converts User entity to UserResponse DTO
    // this mapping lives here so the service just calls UserResponse.from(user)
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
