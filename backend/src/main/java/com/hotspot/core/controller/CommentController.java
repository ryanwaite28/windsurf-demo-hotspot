package com.hotspot.core.controller;

import com.hotspot.common.dto.ApiResponse;
import com.hotspot.common.dto.PagedResponse;
import com.hotspot.core.dto.social.CommentResponse;
import com.hotspot.core.dto.social.CreateCommentRequest;
import com.hotspot.core.dto.social.EditCommentRequest;
import com.hotspot.core.dto.user.ReportRequest;
import com.hotspot.core.entity.User;
import com.hotspot.core.service.CommentService;
import com.hotspot.security.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/social")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/threads/{threadId}/comments")
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable UUID threadId,
            @Valid @RequestBody CreateCommentRequest request) {
        User currentUser = AuthenticatedUser.get();
        CommentResponse response = commentService.createComment(currentUser.getId(), threadId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Comment created", response));
    }

    @GetMapping("/threads/{threadId}/comments")
    public ResponseEntity<ApiResponse<PagedResponse<CommentResponse>>> getComments(
            @PathVariable UUID threadId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<CommentResponse> comments = commentService.getComments(threadId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(comments)));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> editComment(
            @PathVariable UUID commentId,
            @Valid @RequestBody EditCommentRequest request) {
        User currentUser = AuthenticatedUser.get();
        CommentResponse response = commentService.editComment(currentUser.getId(), commentId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable UUID commentId) {
        User currentUser = AuthenticatedUser.get();
        commentService.deleteComment(currentUser.getId(), commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted"));
    }

    @PostMapping("/comments/{commentId}/report")
    public ResponseEntity<ApiResponse<Void>> reportComment(
            @PathVariable UUID commentId,
            @Valid @RequestBody ReportRequest request) {
        User currentUser = AuthenticatedUser.get();
        commentService.reportComment(currentUser.getId(), commentId, request);
        return ResponseEntity.ok(ApiResponse.success("Comment reported"));
    }

    @PostMapping("/comments/{commentId}/mute")
    public ResponseEntity<ApiResponse<Void>> muteComment(@PathVariable UUID commentId) {
        User currentUser = AuthenticatedUser.get();
        commentService.muteComment(currentUser.getId(), commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment muted"));
    }

    @DeleteMapping("/comments/{commentId}/mute")
    public ResponseEntity<ApiResponse<Void>> unmuteComment(@PathVariable UUID commentId) {
        User currentUser = AuthenticatedUser.get();
        commentService.unmuteComment(currentUser.getId(), commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment unmuted"));
    }
}
