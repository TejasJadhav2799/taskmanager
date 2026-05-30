package com.thinkalike.taskmanager.controller;

import com.thinkalike.taskmanager.dto.UserRequest;
import com.thinkalike.taskmanager.dto.UserResponse;
import com.thinkalike.taskmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    // notice we inject the INTERFACE not the implementation
    // the controller doesn't know UserServiceImpl exists
    // this is loose coupling — swap implementations without touching the controller

    // POST /api/users
    // @Valid triggers the validation annotations on UserRequest
    // @NotBlank, @Email, @Size will automatically reject bad input
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
        // 201 Created — not 200 OK — because we created a new resource
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
        // 200 OK
    }

    // GET /api/users?email=tejas@test.com
    @GetMapping("/search")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        UserResponse response = userService.getUserByEmail(email);
        return  ResponseEntity.ok(response);

    }

    // GET /api/users
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // PUT /api/users/{id}
    @PutMapping
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    // DELETE /api/users/{id}
    @DeleteMapping
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
        // 204 No Content — successful deletion returns no body
    }
}
