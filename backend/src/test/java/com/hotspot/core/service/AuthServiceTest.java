package com.hotspot.core.service;

import com.hotspot.common.exception.BadRequestException;
import com.hotspot.common.exception.ConflictException;
import com.hotspot.core.dto.auth.AuthResponse;
import com.hotspot.core.dto.auth.LoginRequest;
import com.hotspot.core.dto.auth.SignupRequest;
import com.hotspot.core.entity.User;
import com.hotspot.core.repository.UserPrivacySettingsRepository;
import com.hotspot.core.repository.UserRepository;
import com.hotspot.core.repository.UserSecuritySettingsRepository;
import com.hotspot.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPrivacySettingsRepository privacySettingsRepository;

    @Mock
    private UserSecuritySettingsRepository securitySettingsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthService authService;

    private SignupRequest signupRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        signupRequest = SignupRequest.builder()
            .email("test@example.com")
            .displayName("Test User")
            .username("testuser")
            .password("password123")
            .confirmPassword("password123")
            .build();

        loginRequest = LoginRequest.builder()
            .email("test@example.com")
            .password("password123")
            .build();

        testUser = User.builder()
            .id(UUID.randomUUID())
            .email("test@example.com")
            .username("testuser")
            .displayName("Test User")
            .passwordHash("encoded_password")
            .build();
    }

    @Test
    void signup_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(privacySettingsRepository.save(any())).thenReturn(null);
        when(securitySettingsRepository.save(any())).thenReturn(null);
        when(jwtTokenProvider.generateAccessToken(any(UUID.class), anyString())).thenReturn("access_token");
        when(jwtTokenProvider.generateRefreshToken(any(UUID.class))).thenReturn("refresh_token");

        AuthResponse response = authService.signup(signupRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signup_PasswordMismatch_ThrowsBadRequest() {
        signupRequest.setConfirmPassword("different_password");

        assertThatThrownBy(() -> authService.signup(signupRequest))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Passwords do not match");
    }

    @Test
    void signup_DuplicateEmail_ThrowsConflict() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.signup(signupRequest))
            .isInstanceOf(ConflictException.class)
            .hasMessage("Email is already in use");
    }

    @Test
    void signup_DuplicateUsername_ThrowsConflict() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThatThrownBy(() -> authService.signup(signupRequest))
            .isInstanceOf(ConflictException.class)
            .hasMessage("Username is already taken");
    }

    @Test
    void login_Success() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(any(UUID.class), anyString())).thenReturn("access_token");
        when(jwtTokenProvider.generateRefreshToken(any(UUID.class))).thenReturn("refresh_token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("access_token");
        assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void login_InvalidEmail_ThrowsBadRequest() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Invalid email or password");
    }

    @Test
    void login_WrongPassword_ThrowsBadRequest() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> authService.login(loginRequest))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Invalid email or password");
    }
}
