package com.thinkalike.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thinkalike.taskmanager.dto.LoginRequest;
import com.thinkalike.taskmanager.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
@Transactional
class UserControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FilterChainProxy filterChainProxy;

    // create ObjectMapper manually — avoids Spring Boot 4.x
    // Jackson 3.x bean type conflict entirely
    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;
    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(filterChainProxy)
                .build();

        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setName("Tejas");
        registerRequest.setEmail("tejas@thinkalike.com");
        registerRequest.setPassword("secret123");

        String registerResponse = mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // extract token from the register response
        authToken = objectMapper.readTree(registerResponse)
                .get("token")
                .asText();
    }

    @Test
    @DisplayName("GET /api/users should return 401 when no token")
    void getAllUsers_NoToken_Returns401() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /api/users should return 200 with valid token")
    void getAllUsers_WithToken_Returns200() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/auth/register returns 201 with token")
    void register_Returns201() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Rahul");
        request.setEmail("rahul@thinkalike.com");
        request.setPassword("secret123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("MEMBER"));
    }

    @Test
    @DisplayName("POST /api/auth/login returns 200 with token")
    void login_Returns200() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("tejas@thinkalike.com");
        request.setPassword("secret123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @DisplayName("POST /api/auth/login returns 400 with wrong password")
    void login_WrongPassword_Returns400() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("tejas@thinkalike.com");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/register returns 400 with invalid email")
    void register_InvalidEmail_Returns400() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("Test");
        request.setEmail("notanemail");
        request.setPassword("secret123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}