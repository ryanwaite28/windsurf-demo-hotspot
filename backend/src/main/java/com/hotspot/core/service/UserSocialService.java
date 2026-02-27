package com.hotspot.core.service;

import com.hotspot.common.dto.PagedResponse;
import com.hotspot.common.exception.BadRequestException;
import com.hotspot.common.exception.ConflictException;
import com.hotspot.common.exception.ResourceNotFoundException;
import com.hotspot.core.dto.user.ReportRequest;
import com.hotspot.core.dto.user.UserProfileResponse;
import com.hotspot.core.entity.*;
import com.hotspot.core.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserSocialService {

    private final UserRepository userRepository;
    private final UserFollowRepository followRepository;
    private final UserMuteRepository muteRepository;
    private final UserBlockRepository blockRepository;
    private final UserReportRepository reportRepository;

    @Transactional
    public void followUser(UUID followerId, UUID followingId) {
        validateNotSelf(followerId, followingId, "follow");
        User follower = findUser(followerId);
        User following = findUser(followingId);

        if (blockRepository.existsByBlockerIdAndBlockedId(followingId, followerId)) {
            throw new BadRequestException("Cannot follow this user");
        }

        if (followRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new ConflictException("Already following this user");
        }

        UserFollow follow = UserFollow.builder()
            .follower(follower)
            .following(following)
            .build();
        followRepository.save(follow);
    }

    @Transactional
    public void unfollowUser(UUID followerId, UUID followingId) {
        UserFollow follow = followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
            .orElseThrow(() -> new ResourceNotFoundException("Not following this user"));
        followRepository.delete(follow);
    }

    @Transactional
    public void muteUser(UUID muterId, UUID mutedId) {
        validateNotSelf(muterId, mutedId, "mute");
        User muter = findUser(muterId);
        User muted = findUser(mutedId);

        if (muteRepository.existsByMuterIdAndMutedId(muterId, mutedId)) {
            throw new ConflictException("Already muted this user");
        }

        UserMute mute = UserMute.builder()
            .muter(muter)
            .muted(muted)
            .build();
        muteRepository.save(mute);
    }

    @Transactional
    public void unmuteUser(UUID muterId, UUID mutedId) {
        UserMute mute = muteRepository.findByMuterIdAndMutedId(muterId, mutedId)
            .orElseThrow(() -> new ResourceNotFoundException("User is not muted"));
        muteRepository.delete(mute);
    }

    @Transactional
    public void blockUser(UUID blockerId, UUID blockedId) {
        validateNotSelf(blockerId, blockedId, "block");
        User blocker = findUser(blockerId);
        User blocked = findUser(blockedId);

        if (blockRepository.existsByBlockerIdAndBlockedId(blockerId, blockedId)) {
            throw new ConflictException("Already blocked this user");
        }

        // Also remove follow relationships in both directions
        followRepository.findByFollowerIdAndFollowingId(blockerId, blockedId)
            .ifPresent(followRepository::delete);
        followRepository.findByFollowerIdAndFollowingId(blockedId, blockerId)
            .ifPresent(followRepository::delete);

        UserBlock block = UserBlock.builder()
            .blocker(blocker)
            .blocked(blocked)
            .build();
        blockRepository.save(block);
    }

    @Transactional
    public void unblockUser(UUID blockerId, UUID blockedId) {
        UserBlock block = blockRepository.findByBlockerIdAndBlockedId(blockerId, blockedId)
            .orElseThrow(() -> new ResourceNotFoundException("User is not blocked"));
        blockRepository.delete(block);
    }

    @Transactional
    public void reportUser(UUID reporterId, UUID reportedId, ReportRequest request) {
        validateNotSelf(reporterId, reportedId, "report");
        User reporter = findUser(reporterId);
        User reported = findUser(reportedId);

        UserReport report = UserReport.builder()
            .reporter(reporter)
            .reported(reported)
            .reason(request.getReason())
            .details(request.getDetails())
            .build();
        reportRepository.save(report);
    }

    public PagedResponse<UserProfileResponse> getFollowers(UUID userId, Pageable pageable) {
        findUser(userId);
        Page<UserFollow> follows = followRepository.findByFollowingId(userId, pageable);
        Page<UserProfileResponse> profiles = follows.map(f -> mapToBasicProfile(f.getFollower()));
        return PagedResponse.from(profiles);
    }

    public PagedResponse<UserProfileResponse> getFollowing(UUID userId, Pageable pageable) {
        findUser(userId);
        Page<UserFollow> follows = followRepository.findByFollowerId(userId, pageable);
        Page<UserProfileResponse> profiles = follows.map(f -> mapToBasicProfile(f.getFollowing()));
        return PagedResponse.from(profiles);
    }

    private User findUser(UUID userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        if (user.getDeletedAt() != null) {
            throw new ResourceNotFoundException("User", "id", userId);
        }
        return user;
    }

    private void validateNotSelf(UUID userId1, UUID userId2, String action) {
        if (userId1.equals(userId2)) {
            throw new BadRequestException("Cannot " + action + " yourself");
        }
    }

    private UserProfileResponse mapToBasicProfile(User user) {
        return UserProfileResponse.builder()
            .id(user.getId().toString())
            .username(user.getUsername())
            .displayName(user.getDisplayName())
            .profilePictureUrl(user.getProfilePictureUrl())
            .bio(user.getBio())
            .build();
    }
}
