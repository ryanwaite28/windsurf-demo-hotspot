package com.hotspot.core.service;

import com.hotspot.common.exception.BadRequestException;
import com.hotspot.common.exception.ForbiddenException;
import com.hotspot.common.exception.ResourceNotFoundException;
import com.hotspot.core.dto.social.CreateThreadRequest;
import com.hotspot.core.dto.social.EditThreadRequest;
import com.hotspot.core.dto.social.ThreadResponse;
import com.hotspot.core.entity.Thread;
import com.hotspot.core.entity.User;
import com.hotspot.core.enums.ThreadType;
import com.hotspot.core.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThreadServiceTest {

    @Mock
    private ThreadRepository threadRepository;
    @Mock
    private ThreadMediaRepository mediaRepository;
    @Mock
    private ThreadPollRepository pollRepository;
    @Mock
    private ThreadPollOptionRepository pollOptionRepository;
    @Mock
    private ThreadPollVoteRepository pollVoteRepository;
    @Mock
    private ThreadLikeRepository likeRepository;
    @Mock
    private ThreadReactionRepository reactionRepository;
    @Mock
    private ThreadCommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ThreadService threadService;

    private User testUser;
    private UUID userId;
    private Thread testThread;
    private UUID threadId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        threadId = UUID.randomUUID();

        testUser = User.builder()
            .id(userId)
            .email("test@example.com")
            .username("testuser")
            .displayName("Test User")
            .build();

        testThread = Thread.builder()
            .id(threadId)
            .author(testUser)
            .content("Test thread content")
            .threadType(ThreadType.ORIGINAL)
            .pinned(false)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }

    @Test
    void createThread_Success() {
        CreateThreadRequest request = CreateThreadRequest.builder()
            .content("New thread content")
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(threadRepository.save(any(Thread.class))).thenReturn(testThread);
        when(mediaRepository.findByThreadIdOrderByDisplayOrder(any())).thenReturn(Collections.emptyList());
        when(pollRepository.findByThreadId(any())).thenReturn(Optional.empty());
        when(likeRepository.countByThreadId(any())).thenReturn(0L);
        when(reactionRepository.countByThreadId(any())).thenReturn(0L);
        when(commentRepository.countByThreadIdAndDeletedAtIsNull(any())).thenReturn(0L);

        ThreadResponse response = threadService.createThread(userId, request);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("Test thread content");
        verify(threadRepository).save(any(Thread.class));
    }

    @Test
    void createThread_EmptyContent_ThrowsBadRequest() {
        CreateThreadRequest request = CreateThreadRequest.builder().build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> threadService.createThread(userId, request))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Thread must have content, media, or a poll");
    }

    @Test
    void editThread_Success() {
        EditThreadRequest request = EditThreadRequest.builder()
            .content("Updated content")
            .build();

        when(threadRepository.findByIdAndDeletedAtIsNull(threadId)).thenReturn(Optional.of(testThread));
        when(threadRepository.save(any(Thread.class))).thenReturn(testThread);
        when(mediaRepository.findByThreadIdOrderByDisplayOrder(any())).thenReturn(Collections.emptyList());
        when(pollRepository.findByThreadId(any())).thenReturn(Optional.empty());
        when(likeRepository.countByThreadId(any())).thenReturn(0L);
        when(reactionRepository.countByThreadId(any())).thenReturn(0L);
        when(commentRepository.countByThreadIdAndDeletedAtIsNull(any())).thenReturn(0L);

        ThreadResponse response = threadService.editThread(userId, threadId, request);

        assertThat(response).isNotNull();
        verify(threadRepository).save(any(Thread.class));
    }

    @Test
    void editThread_NotOwner_ThrowsForbidden() {
        EditThreadRequest request = EditThreadRequest.builder().content("Updated").build();
        UUID otherUserId = UUID.randomUUID();

        when(threadRepository.findByIdAndDeletedAtIsNull(threadId)).thenReturn(Optional.of(testThread));

        assertThatThrownBy(() -> threadService.editThread(otherUserId, threadId, request))
            .isInstanceOf(ForbiddenException.class)
            .hasMessage("You can only edit your own threads");
    }

    @Test
    void editThread_PastEditWindow_ThrowsBadRequest() {
        testThread.setCreatedAt(Instant.now().minus(15, ChronoUnit.MINUTES));
        EditThreadRequest request = EditThreadRequest.builder().content("Updated").build();

        when(threadRepository.findByIdAndDeletedAtIsNull(threadId)).thenReturn(Optional.of(testThread));

        assertThatThrownBy(() -> threadService.editThread(userId, threadId, request))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Threads can only be edited within 10 minutes of creation");
    }

    @Test
    void deleteThread_Success() {
        when(threadRepository.findByIdAndDeletedAtIsNull(threadId)).thenReturn(Optional.of(testThread));
        when(threadRepository.save(any(Thread.class))).thenReturn(testThread);

        threadService.deleteThread(userId, threadId);

        assertThat(testThread.getDeletedAt()).isNotNull();
        verify(threadRepository).save(any(Thread.class));
    }

    @Test
    void deleteThread_NotOwner_ThrowsForbidden() {
        UUID otherUserId = UUID.randomUUID();
        when(threadRepository.findByIdAndDeletedAtIsNull(threadId)).thenReturn(Optional.of(testThread));

        assertThatThrownBy(() -> threadService.deleteThread(otherUserId, threadId))
            .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void repostThread_Success() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(threadRepository.findByIdAndDeletedAtIsNull(threadId)).thenReturn(Optional.of(testThread));

        Thread repost = Thread.builder()
            .id(UUID.randomUUID())
            .author(testUser)
            .threadType(ThreadType.REPOST)
            .parentThread(testThread)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

        when(threadRepository.save(any(Thread.class))).thenReturn(repost);
        when(mediaRepository.findByThreadIdOrderByDisplayOrder(any())).thenReturn(Collections.emptyList());
        when(pollRepository.findByThreadId(any())).thenReturn(Optional.empty());
        when(likeRepository.countByThreadId(any())).thenReturn(0L);
        when(reactionRepository.countByThreadId(any())).thenReturn(0L);
        when(commentRepository.countByThreadIdAndDeletedAtIsNull(any())).thenReturn(0L);

        ThreadResponse response = threadService.repostThread(userId, threadId);

        assertThat(response).isNotNull();
        assertThat(response.getThreadType()).isEqualTo(ThreadType.REPOST);
    }

    @Test
    void getThread_NotFound() {
        when(threadRepository.findByIdAndDeletedAtIsNull(threadId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> threadService.getThread(threadId))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
