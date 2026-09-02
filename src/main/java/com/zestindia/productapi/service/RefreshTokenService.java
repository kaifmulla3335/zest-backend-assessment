package com.zestindia.productapi.service;

import com.zestindia.productapi.entity.RefreshToken;
import com.zestindia.productapi.entity.User;
import com.zestindia.productapi.exception.ResourceNotFoundException;
import com.zestindia.productapi.repository.RefreshTokenRepository;
import com.zestindia.productapi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(jwtUtil.generateRefreshToken())
                .user(user)
                .expiryDate(Instant.now().plusMillis(jwtUtil.getRefreshTokenExpirationMs()))
                .revoked(false)
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyAndGet(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new ResourceNotFoundException("Refresh token expired or revoked, please login again");
        }
        return refreshToken;
    }

    public void revoke(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}