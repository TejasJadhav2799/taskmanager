package com.thinkalike.taskmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data                           // Lombok: generates getters, setters, toString, equals, hashCode
@Builder                        // Lombok: gives you User.builder().name("John").build() pattern
@NoArgsConstructor              // Lombok: generates empty constructor (required by JPA)
@AllArgsConstructor             // Lombok: generates constructor with all fields
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // IDENTITY means PostgreSQL auto-increments this — you never set it manually
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    // unique = true creates a UNIQUE constraint in PostgreSQL
    // this means two users cannot have the same email
    private String email;

    @Column(nullable = false)
    // we will store a hashed password here in Phase 2 (never plain text)
    private String password;

    @Enumerated(EnumType.STRING)
    // EnumType.STRING stores "ADMIN" or "MEMBER" as text in DB
    // EnumType.ORDINAL would store 0 or 1 — never use that, it breaks if you reorder enums
    @Column(nullable = false)
    private Role role;

    @Column(name = "created_at", updatable = false)
    // updatable = false means once set, this column is never changed by JPA
    private LocalDateTime createdAt;

    @PrePersist
    // @PrePersist runs automatically just before JPA inserts this entity
    // perfect for setting timestamps without you having to remember to do it manually
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // the Role enum lives inside User since it's tightly related
    public enum Role {
        ADMIN,
        MEMBER
    }

}
