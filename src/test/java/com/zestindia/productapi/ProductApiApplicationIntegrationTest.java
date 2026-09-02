package com.zestindia.productapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestindia.productapi.dto.LoginRequest;
import com.zestindia.productapi.dto.ProductRequest;
import com.zestindia.productapi.entity.Role;
import com.zestindia.productapi.entity.User;
import com.zestindia.productapi.repository.RefreshTokenRepository;
import com.zestindia.productapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductApiApplicationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        User admin = User.builder()
                .username("integrationadmin")
                .password(passwordEncoder.encode("Test@123"))
                .role(Role.ADMIN)
                .build();
        userRepository.save(admin);

        LoginRequest loginRequest = LoginRequest.builder()
                .username("integrationadmin")
                .password("Test@123")
                .build();

        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        adminToken = objectMapper.readTree(loginResponse).get("accessToken").asText();
    }

    @Test
    void fullCrudFlow_shouldWorkEndToEnd() throws Exception {
        ProductRequest createRequest = ProductRequest.builder()
                .productName("Integration Test Laptop")
                .createdBy("integrationadmin")
                .build();

        // CREATE
        String response = mockMvc.perform(post("/api/v1/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").value("Integration Test Laptop"))
                .andReturn().getResponse().getContentAsString();

        Long productId = objectMapper.readTree(response).get("id").asLong();

        // READ
        mockMvc.perform(get("/api/v1/products/" + productId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Integration Test Laptop"));

        // UPDATE
        ProductRequest updateRequest = ProductRequest.builder()
                .productName("Updated Laptop")
                .createdBy("integrationadmin")
                .build();

        mockMvc.perform(put("/api/v1/products/" + productId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Updated Laptop"));

        // DELETE
        mockMvc.perform(delete("/api/v1/products/" + productId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        // VERIFY DELETED
        mockMvc.perform(get("/api/v1/products/" + productId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProducts_withoutAuth_shouldReturn401or403() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().is4xxClientError());
    }
}