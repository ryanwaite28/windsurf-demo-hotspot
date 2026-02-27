package com.hotspot.core.service;

import com.hotspot.common.exception.BadRequestException;
import com.hotspot.common.exception.ConflictException;
import com.hotspot.common.exception.ResourceNotFoundException;
import com.hotspot.core.entity.User;
import com.hotspot.core.entity.UserBlock;
import com.hotspot.core.entity.UserFollow;
import com.hotspot.core.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSocialServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserFollowRepository followRepository;

    @Mock
    private UserMuteRepository muteRepository;

    @Mock
    private UserBlockRepository blockRepository;

    @Mock
    private UserReportRepository reportRepository;

    @InjectMocks
    private UserSocialService userSocialService;

    private User user1;
    private User user2;
    private UUID user1Id;
    private UUID user2Id;

    @BeforeEach
    void setUp() {
        user1Id = UUID.randomUUID();
        user2Id = UUID.randomUUID();

        user1 = User.builder().id(user1Id).email("user1@example.com").username("user1").displayName("User 1").build();
        user2 = User.builder().id(user2Id).email("user2@example.com").username("user2").displayName("User 2").build();
    }

    @Test
    void followUser_Success() {
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));
        when(userRepository.findById(user2Id)).thenReturn(Optional.of(user2));
        when(blockRepository.existsByBlockerIdAndBlockedId(user2Id, user1Id)).thenReturn(false);
        when(followRepository.existsByFollowerIdAndFollowingId(user1Id, user2Id)).thenReturn(false);
        when(followRepository.save(any(UserFollow.class))).thenReturn(new UserFollow());

        userSocialService.followUser(user1Id, user2Id);

        verify(followRepository).save(any(UserFollow.class));
    }

    @Test
    void followUser_Self_ThrowsBadRequest() {
        assertThatThrownBy(() -> userSocialService.followUser(user1Id, user1Id))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Cannot follow yourself");
    }

    @Test
    void followUser_AlreadyFollowing_ThrowsConflict() {
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));
        when(userRepository.findById(user2Id)).thenReturn(Optional.of(user2));
        when(blockRepository.existsByBlockerIdAndBlockedId(user2Id, user1Id)).thenReturn(false);
        when(followRepository.existsByFollowerIdAndFollowingId(user1Id, user2Id)).thenReturn(true);

        assertThatThrownBy(() -> userSocialService.followUser(user1Id, user2Id))
            .isInstanceOf(ConflictException.class)
            .hasMessage("Already following this user");
    }

    @Test
    void followUser_BlockedByTarget_ThrowsBadRequest() {
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));
        when(userRepository.findById(user2Id)).thenReturn(Optional.of(user2));
        when(blockRepository.existsByBlockerIdAndBlockedId(user2Id, user1Id)).thenReturn(true);

        assertThatThrownBy(() -> userSocialService.followUser(user1Id, user2Id))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Cannot follow this user");
    }

    @Test
    void unfollowUser_Success() {
        UserFollow follow = UserFollow.builder().id(UUID.randomUUID()).follower(user1).following(user2).build();
        when(followRepository.findByFollowerIdAndFollowingId(user1Id, user2Id)).thenReturn(Optional.of(follow));

        userSocialService.unfollowUser(user1Id, user2Id);

        verify(followRepository).delete(follow);
    }

    @Test
    void unfollowUser_NotFollowing_ThrowsNotFound() {
        when(followRepository.findByFollowerIdAndFollowingId(user1Id, user2Id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userSocialService.unfollowUser(user1Id, user2Id))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void blockUser_RemovesFollowRelationships() {
        when(userRepository.findById(user1Id)).thenReturn(Optional.of(user1));
        when(userRepository.findById(user2Id)).thenReturn(Optional.of(user2));
        when(blockRepository.existsByBlockerIdAndBlockedId(user1Id, user2Id)).thenReturn(false);
        when(followRepository.findByFollowerIdAndFollowingId(user1Id, user2Id)).thenReturn(Optional.of(new UserFollow()));
        when(followRepository.findByFollowerIdAndFollowingId(user2Id, user1Id)).thenReturn(Optional.of(new UserFollow()));
        when(blockRepository.save(any(UserBlock.class))).thenReturn(new UserBlock());

        userSocialService.blockUser(user1Id, user2Id);

        verify(followRepository, times(2)).delete(any(UserFollow.class));
        verify(blockRepository).save(any(UserBlock.class));
    }
}
