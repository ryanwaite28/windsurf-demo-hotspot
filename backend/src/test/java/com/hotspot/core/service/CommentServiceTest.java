package com.hotspot.core.service;

import com.hotspot.common.exception.BadRequestException;
import com.hotspot.common.exception.ForbiddenException;
import com.hotspot.common.exception.ResourceNotFoundException;
import com.hotspot.core.dto.social.CommentResponse;
import com.hotspot.core.dto.social.CreateCommentRequest;
import com.hotspot.core.dto.social.EditCommentRequest;
import com.hotspot.core.entity.Thread;
import com.hotspot.core.entity.ThreadComment;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private ThreadRepository threadRepository;
    @Mock
    private ThreadCommentRepository commentRepository;
    @Mock
    private CommentReportRepository commentReportRepository;
    @Mock
    private CommentMuteRepository commentMuteRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private UUID userId;
    private Thread testThread;
    private UUID threadId;
    private ThreadComment testComment;
    private UUID commentId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        threadId = UUID.randomUUID();
        commentId = UUID.randomUUID();

        testUser = User.builder()
            .id(userId)
            .email("test@example.com")
            .username("testuser")
            .displayName("Test User")
            .build();

        testThread = Thread.builder()
            .id(threadId)
            .author(testUser)
            .content("Test thread")
            .threadType(ThreadType.ORIGINAL)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

        testComment = ThreadComment.builder()
            .id(commentId)
            .thread(testThread)
            .author(testUser)
            .content("Test comment")
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
    }

    @Test
    void createComment_Success() {
        CreateCommentRequest request = CreateCommentRequest.builder().content("New comment").build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(threadRepository.findByIdAndDeletedAtIsNull(threadId)).thenReturn(Optional.of(testThread));
        when(commentRepository.save(any(ThreadComment.class))).thenReturn(testComment);

        CommentResponse response = commentService.createComment(userId, threadId, request);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("Test comment");
        verify(commentRepository).save(any(ThreadComment.class));
    }

    @Test
    void editComment_Success() {
        EditCommentRequest request = EditCommentRequest.builder().content("Updated comment").build();

        when(commentRepository.findByIdAndDeletedAtIsNull(commentId)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(ThreadComment.class))).thenReturn(testComment);

        CommentResponse response = commentService.editComment(userId, commentId, request);

        assertThat(response).isNotNull();
        verify(commentRepository).save(any(ThreadComment.class));
    }

    @Test
    void editComment_NotOwner_ThrowsForbidden() {
        UUID otherUserId = UUID.randomUUID();
        EditCommentRequest request = EditCommentRequest.builder().content("Updated").build();

        when(commentRepository.findByIdAndDeletedAtIsNull(commentId)).thenReturn(Optional.of(testComment));

        assertThatThrownBy(() -> commentService.editComment(otherUserId, commentId, request))
            .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void editComment_PastEditWindow_ThrowsBadRequest() {
        testComment.setCreatedAt(Instant.now().minus(15, ChronoUnit.MINUTES));
        EditCommentRequest request = EditCommentRequest.builder().content("Updated").build();

        when(commentRepository.findByIdAndDeletedAtIsNull(commentId)).thenReturn(Optional.of(testComment));

        assertThatThrownBy(() -> commentService.editComment(userId, commentId, request))
            .isInstanceOf(BadRequestException.class)
            .hasMessage("Comments can only be edited within 10 minutes of creation");
    }

    @Test
    void deleteComment_Success() {
        when(commentRepository.findByIdAndDeletedAtIsNull(commentId)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(ThreadComment.class))).thenReturn(testComment);

        commentService.deleteComment(userId, commentId);

        assertThat(testComment.getDeletedAt()).isNotNull();
    }

    @Test
    void deleteComment_NotOwner_ThrowsForbidden() {
        UUID otherUserId = UUID.randomUUID();
        when(commentRepository.findByIdAndDeletedAtIsNull(commentId)).thenReturn(Optional.of(testComment));

        assertThatThrownBy(() -> commentService.deleteComment(otherUserId, commentId))
            .isInstanceOf(ForbiddenException.class);
    }
}
