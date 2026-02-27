package com.hotspot.core.controller;

import com.hotspot.common.dto.ApiResponse;
import com.hotspot.common.dto.PagedResponse;
import com.hotspot.core.dto.user.ReportRequest;
import com.hotspot.core.dto.user.UserProfileResponse;
import com.hotspot.core.entity.User;
import com.hotspot.core.service.UserSocialService;
import com.hotspot.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserSocialController {

    private final UserSocialService userSocialService;

    @PostMapping("/{userId}/follow")
    public ResponseEntity<ApiResponse<Void>> followUser(@PathVariable UUID userId) {
        User currentUser = AuthenticatedUser.get();
        userSocialService.followUser(currentUser.getId(), userId);
        return ResponseEntity.ok(ApiResponse.success("User followed successfully"));
    }

    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<ApiResponse<Void>> unfollowUser(@PathVariable UUID userId) {
        User currentUser = AuthenticatedUser.get();
        userSocialService.unfollowUser(currentUser.getId(), userId);
        return ResponseEntity.ok(ApiResponse.success("User unfollowed successfully"));
    }

    @PostMapping("/{userId}/mute")
    public ResponseEntity<ApiResponse<Void>> muteUser(@PathVariable UUID userId) {
        User currentUser = AuthenticatedUser.get();
        userSocialService.muteUser(currentUser.getId(), userId);
        return ResponseEntity.ok(ApiResponse.success("User muted successfully"));
    }

    @DeleteMapping("/{userId}/mute")
    public ResponseEntity<ApiResponse<Void>> unmuteUser(@PathVariable UUID userId) {
        User currentUser = AuthenticatedUser.get();
        userSocialService.unmuteUser(currentUser.getId(), userId);
        return ResponseEntity.ok(ApiResponse.success("User unmuted successfully"));
    }

    @PostMapping("/{userId}/block")
    public ResponseEntity<ApiResponse<Void>> blockUser(@PathVariable UUID userId) {
        User currentUser = AuthenticatedUser.get();
        userSocialService.blockUser(currentUser.getId(), userId);
        return ResponseEntity.ok(ApiResponse.success("User blocked successfully"));
    }

    @DeleteMapping("/{userId}/block")
    public ResponseEntity<ApiResponse<Void>> unblockUser(@PathVariable UUID userId) {
        User currentUser = AuthenticatedUser.get();
        userSocialService.unblockUser(currentUser.getId(), userId);
        return ResponseEntity.ok(ApiResponse.success("User unblocked successfully"));
    }

    @PostMapping("/{userId}/report")
    public ResponseEntity<ApiResponse<Void>> reportUser(
            @PathVariable UUID userId,
            @Valid @RequestBody ReportRequest request) {
        User currentUser = AuthenticatedUser.get();
        userSocialService.reportUser(currentUser.getId(), userId, request);
        return ResponseEntity.ok(ApiResponse.success("User reported successfully"));
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<ApiResponse<PagedResponse<UserProfileResponse>>> getFollowers(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<UserProfileResponse> followers = userSocialService.getFollowers(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(followers));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<ApiResponse<PagedResponse<UserProfileResponse>>> getFollowing(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PagedResponse<UserProfileResponse> following = userSocialService.getFollowing(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(following));
    }
}
