package com.zestindia.productapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zestindia.productapi.dto.ProductRequest;
import com.zestindia.productapi.dto.ProductResponse;
import com.zestindia.productapi.exception.ResourceNotFoundException;
import com.zestindia.productapi.security.CustomUserDetailsService;
import com.zestindia.productapi.security.JwtUtil;
import com.zestindia.productapi.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(roles = "USER")
    void getProductById_shouldReturn200_whenExists() throws Exception {
        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .productName("Laptop")
                .items(List.of())
                .build();

        when(productService.getProductById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Laptop"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getProductById_shouldReturn404_whenNotFound() throws Exception {
        when(productService.getProductById(99L))
                .thenThrow(new ResourceNotFoundException("Product not found with id: 99"));

        mockMvc.perform(get("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 99"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_shouldReturn201_whenValid() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .productName("Mouse")
                .createdBy("admin")
                .build();

        ProductResponse response = ProductResponse.builder()
                .id(2L)
                .productName("Mouse")
                .items(List.of())
                .build();

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productName").value("Mouse"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_shouldReturn400_whenProductNameBlank() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .productName("")
                .createdBy("admin")
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_shouldReturn200_whenValid() throws Exception {
        ProductRequest request = ProductRequest.builder()
                .productName("Laptop Pro")
                .createdBy("admin")
                .build();

        ProductResponse response = ProductResponse.builder()
                .id(1L)
                .productName("Laptop Pro")
                .items(List.of())
                .build();

        when(productService.updateProduct(eq(1L), any(ProductRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Laptop Pro"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_shouldReturn204_whenExists() throws Exception {
        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());
    }
}