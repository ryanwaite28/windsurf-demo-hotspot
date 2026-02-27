package com.hotspot.core.service;

import com.hotspot.common.exception.BadRequestException;
import com.hotspot.common.exception.ConflictException;
import com.hotspot.common.exception.ResourceNotFoundException;
import com.hotspot.core.dto.auth.*;
import com.hotspot.core.entity.User;
import com.hotspot.core.entity.UserPrivacySettings;
import com.hotspot.core.entity.UserSecuritySettings;
import com.hotspot.core.repository.UserPrivacySettingsRepository;
import com.hotspot.core.repository.UserRepository;
import com.hotspot.core.repository.UserSecuritySettingsRepository;
import com.hotspot.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserPrivacySettingsRepository privacySettingsRepository;
    private final UserSecuritySettingsRepository securitySettingsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse signup(SignupRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email is already in use");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username is already taken");
        }

        User user = User.builder()
            .email(request.getEmail().toLowerCase().trim())
            .username(request.getUsername().toLowerCase().trim())
            .displayName(request.getDisplayName().trim())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .build();

        user = userRepository.save(user);

        UserPrivacySettings privacySettings = UserPrivacySettings.builder()
            .user(user)
            .build();
        privacySettingsRepository.save(privacySettings);

        UserSecuritySettings securitySettings = UserSecuritySettings.builder()
            .user(user)
            .build();
        securitySettingsRepository.save(securitySettings);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
            .orElseThrow(() -> new BadRequestException("Invalid email or password"));

        if (user.getDeletedAt() != null) {
            throw new BadRequestException("Invalid email or password");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid email or password");
        }

        return buildAuthResponse(user);
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        String tokenType = jwtTokenProvider.getTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new BadRequestException("Invalid token type");
        }

        UUID userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.getDeletedAt() != null) {
            throw new BadRequestException("Account has been deleted");
        }

        return buildAuthResponse(user);
    }

    public void requestPasswordReset(PasswordResetRequest request) {
        // In production, this would send an email with a reset token via SES
        // For MVP, we just validate the email exists
        userRepository.findByEmail(request.getEmail().toLowerCase().trim())
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));
    }

    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        // In production, this would validate the reset token
        // For MVP, we use a simplified flow
        // TODO: Implement proper token-based password reset with SES
    }

    private AuthResponse buildAuthResponse(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        return AuthResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .user(AuthResponse.UserInfo.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .username(user.getUsername())
                .displayName(user.getDisplayName())
                .build())
            .build();
    }
}
