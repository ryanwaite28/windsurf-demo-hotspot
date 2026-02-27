package com.hotspot.core.service;

import com.hotspot.common.exception.BadRequestException;
import com.hotspot.common.exception.ConflictException;
import com.hotspot.common.exception.ForbiddenException;
import com.hotspot.common.exception.ResourceNotFoundException;
import com.hotspot.core.dto.social.CommentResponse;
import com.hotspot.core.dto.social.CreateCommentRequest;
import com.hotspot.core.dto.social.EditCommentRequest;
import com.hotspot.core.dto.social.ThreadResponse;
import com.hotspot.core.dto.user.ReportRequest;
import com.hotspot.core.entity.*;
import com.hotspot.core.entity.Thread;
import com.hotspot.core.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private static final Duration EDIT_WINDOW = Duration.ofMinutes(10);

    private final ThreadRepository threadRepository;
    private final ThreadCommentRepository commentRepository;
    private final CommentReportRepository commentReportRepository;
    private final CommentMuteRepository commentMuteRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponse createComment(UUID userId, UUID threadId, CreateCommentRequest request) {
        User author = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Thread thread = threadRepository.findByIdAndDeletedAtIsNull(threadId)
            .orElseThrow(() -> new ResourceNotFoundException("Thread", "id", threadId));

        ThreadComment comment = ThreadComment.builder()
            .thread(thread)
            .author(author)
            .content(request.getContent())
            .build();
        comment = commentRepository.save(comment);
        return mapToResponse(comment);
    }

    public Page<CommentResponse> getComments(UUID threadId, Pageable pageable) {
        threadRepository.findByIdAndDeletedAtIsNull(threadId)
            .orElseThrow(() -> new ResourceNotFoundException("Thread", "id", threadId));

        return commentRepository.findByThreadIdAndDeletedAtIsNullOrderByCreatedAtDesc(threadId, pageable)
            .map(this::mapToResponse);
    }

    @Transactional
    public CommentResponse editComment(UUID userId, UUID commentId, EditCommentRequest request) {
        ThreadComment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("You can only edit your own comments");
        }

        if (Duration.between(comment.getCreatedAt(), Instant.now()).compareTo(EDIT_WINDOW) > 0) {
            throw new BadRequestException("Comments can only be edited within 10 minutes of creation");
        }

        comment.setContent(request.getContent());
        comment = commentRepository.save(comment);
        return mapToResponse(comment);
    }

    @Transactional
    public void deleteComment(UUID userId, UUID commentId) {
        ThreadComment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ForbiddenException("You can only delete your own comments");
        }

        comment.setDeletedAt(Instant.now());
        commentRepository.save(comment);
    }

    @Transactional
    public void reportComment(UUID userId, UUID commentId, ReportRequest request) {
        User reporter = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        ThreadComment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        CommentReport report = CommentReport.builder()
            .comment(comment)
            .reporter(reporter)
            .reason(request.getReason())
            .details(request.getDetails())
            .build();
        commentReportRepository.save(report);
    }

    @Transactional
    public void muteComment(UUID userId, UUID commentId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        ThreadComment comment = commentRepository.findByIdAndDeletedAtIsNull(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (commentMuteRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new ConflictException("Comment already muted");
        }

        CommentMute mute = CommentMute.builder()
            .comment(comment)
            .user(user)
            .build();
        commentMuteRepository.save(mute);
    }

    @Transactional
    public void unmuteComment(UUID userId, UUID commentId) {
        CommentMute mute = commentMuteRepository.findByCommentIdAndUserId(commentId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not muted"));
        commentMuteRepository.delete(mute);
    }

    private CommentResponse mapToResponse(ThreadComment comment) {
        User author = comment.getAuthor();
        return CommentResponse.builder()
            .id(comment.getId().toString())
            .threadId(comment.getThread().getId().toString())
            .author(ThreadResponse.AuthorInfo.builder()
                .id(author.getId().toString())
                .username(author.getUsername())
                .displayName(author.getDisplayName())
                .profilePictureUrl(author.getProfilePictureUrl())
                .build())
            .content(comment.getContent())
            .createdAt(comment.getCreatedAt())
            .updatedAt(comment.getUpdatedAt())
            .build();
    }
}
