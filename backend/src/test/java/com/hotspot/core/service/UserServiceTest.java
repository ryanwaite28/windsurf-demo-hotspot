package com.hotspot.core.service;

import com.hotspot.common.exception.ConflictException;
import com.hotspot.common.exception.ResourceNotFoundException;
import com.hotspot.core.dto.user.UpdateProfileRequest;
import com.hotspot.core.dto.user.UserProfileResponse;
import com.hotspot.core.entity.User;
import com.hotspot.core.repository.UserFollowRepository;
import com.hotspot.core.repository.UserPrivacySettingsRepository;
import com.hotspot.core.repository.UserRepository;
import com.hotspot.core.repository.UserSecuritySettingsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPrivacySettingsRepository privacySettingsRepository;

    @Mock
    private UserSecuritySettingsRepository securitySettingsRepository;

    @Mock
    private UserFollowRepository followRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        testUser = User.builder()
            .id(userId)
            .email("test@example.com")
            .username("testuser")
            .displayName("Test User")
            .bio("Hello world")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }

    @Test
    void getProfileByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(followRepository.countByFollowingId(userId)).thenReturn(10L);
        when(followRepository.countByFollowerId(userId)).thenReturn(5L);

        UserProfileResponse profile = userService.getProfileByUsername("testuser");

        assertThat(profile).isNotNull();
        assertThat(profile.getUsername()).isEqualTo("testuser");
        assertThat(profile.getDisplayName()).isEqualTo("Test User");
        assertThat(profile.getFollowersCount()).isEqualTo(10);
        assertThat(profile.getFollowingCount()).isEqualTo(5);
    }

    @Test
    void getProfileByUsername_NotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getProfileByUsername("nonexistent"))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getProfileByUsername_DeletedUser_ThrowsNotFound() {
        testUser.setDeletedAt(Instant.now());
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> userService.getProfileByUsername("testuser"))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateProfile_Success() {
        UpdateProfileRequest request = UpdateProfileRequest.builder()
            .displayName("New Name")
            .bio("New bio")
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(followRepository.countByFollowingId(userId)).thenReturn(0L);
        when(followRepository.countByFollowerId(userId)).thenReturn(0L);

        UserProfileResponse profile = userService.updateProfile(userId, request);

        assertThat(profile).isNotNull();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateProfile_DuplicateUsername_ThrowsConflict() {
        UpdateProfileRequest request = UpdateProfileRequest.builder()
            .username("taken")
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername("taken")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateProfile(userId, request))
            .isInstanceOf(ConflictException.class)
            .hasMessage("Username is already taken");
    }

    @Test
    void deleteAccount_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.deleteAccount(userId);

        verify(userRepository).save(any(User.class));
        assertThat(testUser.getDeletedAt()).isNotNull();
    }
}
