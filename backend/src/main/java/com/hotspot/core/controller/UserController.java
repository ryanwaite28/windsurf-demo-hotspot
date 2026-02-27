package com.hotspot.core.controller;

import com.hotspot.common.dto.ApiResponse;
import com.hotspot.core.dto.user.*;
import com.hotspot.core.entity.User;
import com.hotspot.core.service.UserService;
import com.hotspot.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(@PathVariable String username) {
        UserProfileResponse profile = userService.getProfileByUsername(username);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        User currentUser = AuthenticatedUser.get();
        UserProfileResponse profile = userService.updateProfile(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteAccount() {
        User currentUser = AuthenticatedUser.get();
        userService.deleteAccount(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully"));
    }

    @GetMapping("/me/settings/privacy")
    public ResponseEntity<ApiResponse<PrivacySettingsResponse>> getPrivacySettings() {
        User currentUser = AuthenticatedUser.get();
        PrivacySettingsResponse settings = userService.getPrivacySettings(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(settings));
    }

    @PutMapping("/me/settings/privacy")
    public ResponseEntity<ApiResponse<PrivacySettingsResponse>> updatePrivacySettings(
            @Valid @RequestBody UpdatePrivacySettingsRequest request) {
        User currentUser = AuthenticatedUser.get();
        PrivacySettingsResponse settings = userService.updatePrivacySettings(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(settings));
    }

    @GetMapping("/me/settings/security")
    public ResponseEntity<ApiResponse<SecuritySettingsResponse>> getSecuritySettings() {
        User currentUser = AuthenticatedUser.get();
        SecuritySettingsResponse settings = userService.getSecuritySettings(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(settings));
    }

    @PutMapping("/me/settings/security")
    public ResponseEntity<ApiResponse<SecuritySettingsResponse>> updateSecuritySettings(
            @Valid @RequestBody UpdateSecuritySettingsRequest request) {
        User currentUser = AuthenticatedUser.get();
        SecuritySettingsResponse settings = userService.updateSecuritySettings(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success(settings));
    }
}
