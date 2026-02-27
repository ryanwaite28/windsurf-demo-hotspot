package com.hotspot.core.service;

import com.hotspot.common.exception.BadRequestException;
import com.hotspot.common.exception.ConflictException;
import com.hotspot.common.exception.ResourceNotFoundException;
import com.hotspot.core.dto.user.*;
import com.hotspot.core.entity.User;
import com.hotspot.core.entity.UserPrivacySettings;
import com.hotspot.core.entity.UserSecuritySettings;
import com.hotspot.core.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserPrivacySettingsRepository privacySettingsRepository;
    private final UserSecuritySettingsRepository securitySettingsRepository;
    private final UserFollowRepository followRepository;

    public UserProfileResponse getProfileByUsername(String username) {
        User user = userRepository.findByUsername(username.toLowerCase().trim())
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (user.getDeletedAt() != null) {
            throw new ResourceNotFoundException("User", "username", username);
        }

        return mapToProfileResponse(user);
    }

    public UserProfileResponse getProfileById(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (user.getDeletedAt() != null) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        return mapToProfileResponse(user);
    }

    @Transactional
    public UserProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (request.getDisplayName() != null) {
            user.setDisplayName(request.getDisplayName().trim());
        }

        if (request.getUsername() != null) {
            String newUsername = request.getUsername().toLowerCase().trim();
            if (!newUsername.equals(user.getUsername()) && userRepository.existsByUsername(newUsername)) {
                throw new ConflictException("Username is already taken");
            }
            user.setUsername(newUsername);
        }

        if (request.getBio() != null) {
            user.setBio(request.getBio().trim());
        }

        if (request.getProfilePictureUrl() != null) {
            user.setProfilePictureUrl(request.getProfilePictureUrl());
        }

        if (request.getLocation() != null) {
            user.setLocation(request.getLocation().trim());
        }

        user = userRepository.save(user);
        return mapToProfileResponse(user);
    }

    @Transactional
    public void deleteAccount(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setDeletedAt(Instant.now());
        userRepository.save(user);
    }

    public PrivacySettingsResponse getPrivacySettings(UUID userId) {
        UserPrivacySettings settings = privacySettingsRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Privacy settings not found"));

        return PrivacySettingsResponse.builder()
            .profileVisibility(settings.getProfileVisibility())
            .postsVisibility(settings.getPostsVisibility())
            .locationVisibility(settings.getLocationVisibility())
            .phoneVisibility(settings.getPhoneVisibility())
            .build();
    }

    @Transactional
    public PrivacySettingsResponse updatePrivacySettings(UUID userId, UpdatePrivacySettingsRequest request) {
        UserPrivacySettings settings = privacySettingsRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Privacy settings not found"));

        if (request.getProfileVisibility() != null) {
            settings.setProfileVisibility(request.getProfileVisibility());
        }
        if (request.getPostsVisibility() != null) {
            settings.setPostsVisibility(request.getPostsVisibility());
        }
        if (request.getLocationVisibility() != null) {
            settings.setLocationVisibility(request.getLocationVisibility());
        }
        if (request.getPhoneVisibility() != null) {
            settings.setPhoneVisibility(request.getPhoneVisibility());
        }

        settings = privacySettingsRepository.save(settings);

        return PrivacySettingsResponse.builder()
            .profileVisibility(settings.getProfileVisibility())
            .postsVisibility(settings.getPostsVisibility())
            .locationVisibility(settings.getLocationVisibility())
            .phoneVisibility(settings.getPhoneVisibility())
            .build();
    }

    public SecuritySettingsResponse getSecuritySettings(UUID userId) {
        UserSecuritySettings settings = securitySettingsRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Security settings not found"));

        return SecuritySettingsResponse.builder()
            .twoFactorEnabled(settings.isTwoFactorEnabled())
            .build();
    }

    @Transactional
    public SecuritySettingsResponse updateSecuritySettings(UUID userId, UpdateSecuritySettingsRequest request) {
        UserSecuritySettings settings = securitySettingsRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Security settings not found"));

        if (request.getTwoFactorEnabled() != null) {
            settings.setTwoFactorEnabled(request.getTwoFactorEnabled());
        }

        settings = securitySettingsRepository.save(settings);

        return SecuritySettingsResponse.builder()
            .twoFactorEnabled(settings.isTwoFactorEnabled())
            .build();
    }

    private UserProfileResponse mapToProfileResponse(User user) {
        long followersCount = followRepository.countByFollowingId(user.getId());
        long followingCount = followRepository.countByFollowerId(user.getId());

        return UserProfileResponse.builder()
            .id(user.getId().toString())
            .email(user.getEmail())
            .username(user.getUsername())
            .displayName(user.getDisplayName())
            .bio(user.getBio())
            .profilePictureUrl(user.getProfilePictureUrl())
            .location(user.getLocation())
            .phone(user.getPhone())
            .phoneVerified(user.isPhoneVerified())
            .followersCount(followersCount)
            .followingCount(followingCount)
            .createdAt(user.getCreatedAt())
            .build();
    }
}
