package com.thinkalike.taskmanager.service;

import com.thinkalike.taskmanager.dto.UserRequest;
import com.thinkalike.taskmanager.dto.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(UserRequest request);

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    List<UserResponse> getAllUsers();

    UserResponse updateUser(Long id, UserRequest request);

    void deleteUser(Long id);
}
