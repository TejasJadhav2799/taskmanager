package com.thinkalike.taskmanager.repository;

import com.thinkalike.taskmanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring reads this method name and generates:
    // SELECT * FROM users WHERE email = ?
    // no implementation needed — just the method signature
    Optional<User> findByEmail(String email);

    // SELECT * FROM users WHERE email = ? (returns true/false)
    // we'll use this to check if email is already taken during registration
    Boolean existsByEmail(String email);
}
