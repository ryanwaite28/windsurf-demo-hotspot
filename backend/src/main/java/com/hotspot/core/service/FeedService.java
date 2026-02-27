package com.hotspot.core.service;

import com.hotspot.core.dto.social.ThreadResponse;
import com.hotspot.core.entity.Thread;
import com.hotspot.core.entity.UserFollow;
import com.hotspot.core.repository.ThreadRepository;
import com.hotspot.core.repository.UserFollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final ThreadRepository threadRepository;
    private final UserFollowRepository followRepository;
    private final ThreadService threadService;

    public Page<ThreadResponse> getFeed(UUID userId, Pageable pageable) {
        List<UUID> followingIds = followRepository.findByFollowerId(userId, Pageable.unpaged())
            .getContent()
            .stream()
            .map(follow -> follow.getFollowing().getId())
            .toList();

        if (followingIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<Thread> threads = threadRepository.findByAuthorIdInAndDeletedAtIsNull(followingIds, pageable);
        return threads.map(threadService::mapToResponse);
    }
}
