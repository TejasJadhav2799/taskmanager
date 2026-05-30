package com.thinkalike.taskmanager.service;

import com.thinkalike.taskmanager.dto.UserRequest;
import com.thinkalike.taskmanager.dto.UserResponse;
import com.thinkalike.taskmanager.exception.ResourceNotFoundException;
import com.thinkalike.taskmanager.model.User;
import com.thinkalike.taskmanager.repository.UserRepository;
import com.thinkalike.taskmanager.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
// @ExtendWith(MockitoExtension.class) tells JUnit to use Mockito
// no Spring context loaded — pure Java, runs in milliseconds
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    // @Mock creates a fake UserRepository
    // no real database — we control exactly what it returns

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;
    // @InjectMocks creates a real UserServiceImpl
    // and injects the mocks above into it automatically

    private User mockUser;
    private UserRequest userRequest;

    @BeforeEach
        // @BeforeEach runs before every single test method
        // sets up fresh test data each time
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .name("Tejas")
                .email("tejas@thinkalike.com")
                .password("hashedpassword")
                .role(User.Role.MEMBER)
                .build();

        userRequest = new UserRequest();
        userRequest.setName("Tejas");
        userRequest.setEmail("tejas@thinkalike.com");
        userRequest.setPassword("secret123");
    }

    @Test
    @DisplayName("Should create user successfully when email is not taken")
    void createUser_Success() {
        // ARRANGE — set up what the mocks should return
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // ACT — call the actual method
        UserResponse response = userService.createUser(userRequest);

        // ASSERT — verify the result
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Tejas");
        assertThat(response.getEmail()).isEqualTo("tejas@thinkalike.com");
        assertThat(response.getRole()).isEqualTo(User.Role.MEMBER);

        // verify repository was called exactly once
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void createUser_EmailAlreadyExists_ThrowsException() {
        // ARRANGE — simulate email already taken
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // ACT + ASSERT — verify exception is thrown
        assertThatThrownBy(() -> userService.createUser(userRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User already exists with email");

        // verify save was NEVER called — we stopped before reaching it
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return user when found by id")
    void getUserById_Success() {
        // ARRANGE
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // ACT
        UserResponse response = userService.getUserById(1L);

        // ASSERT
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Tejas");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user not found")
    void getUserById_NotFound_ThrowsException() {
        // ARRANGE — simulate user not found
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with id: 999");
    }

    @Test
    @DisplayName("Should return all users")
    void getAllUsers_Success() {
        // ARRANGE
        when(userRepository.findAll()).thenReturn(List.of(mockUser));

        // ACT
        List<UserResponse> responses = userService.getAllUsers();

        // ASSERT
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("Tejas");
    }

    @Test
    @DisplayName("Should return empty list when no users exist")
    void getAllUsers_EmptyList() {
        // ARRANGE
        when(userRepository.findAll()).thenReturn(List.of());

        // ACT
        List<UserResponse> responses = userService.getAllUsers();

        // ASSERT
        assertThat(responses).isEmpty();
    }

    @Test
    @DisplayName("Should delete user when found")
    void deleteUser_Success() {
        // ARRANGE
        when(userRepository.existsById(1L)).thenReturn(true);

        // ACT
        userService.deleteUser(1L);

        // ASSERT — verify deleteById was called once
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void deleteUser_NotFound_ThrowsException() {
        // ARRANGE
        when(userRepository.existsById(999L)).thenReturn(false);

        // ACT + ASSERT
        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("User not found with id: 999");

        // verify deleteById was NEVER called
        verify(userRepository, never()).deleteById(any());
    }
}