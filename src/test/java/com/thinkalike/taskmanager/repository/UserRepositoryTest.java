package com.thinkalike.taskmanager.repository;

import com.thinkalike.taskmanager.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
// @SpringBootTest loads the full context
// uses H2 from test/resources/application-test.properties
@ActiveProfiles("test")
@Transactional
// @Transactional on test class rolls back all changes after each test
// database stays clean between tests
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Should find user by email")
    void findByEmail_Success() {
        // ARRANGE — save directly to H2
        User user = User.builder()
                .name("Tejas")
                .email("tejas@thinkalike.com")
                .password("hashedpassword")
                .role(User.Role.MEMBER)
                .build();
        userRepository.save(user);

        // ACT
        Optional<User> found = userRepository
                .findByEmail("tejas@thinkalike.com");

        // ASSERT
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Tejas");
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void findByEmail_NotFound() {
        Optional<User> found = userRepository
                .findByEmail("nobody@test.com");

        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should return true when email exists")
    void existsByEmail_True() {
        User user = User.builder()
                .name("Tejas")
                .email("exists@thinkalike.com")
                .password("hashed")
                .role(User.Role.MEMBER)
                .build();
        userRepository.save(user);

        boolean exists = userRepository
                .existsByEmail("exists@thinkalike.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void existsByEmail_False() {
        boolean exists = userRepository
                .existsByEmail("ghost@test.com");

        assertThat(exists).isFalse();
    }
}