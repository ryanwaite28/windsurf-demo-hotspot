package com.hotspot.core.controller;

import com.hotspot.common.dto.ApiResponse;
import com.hotspot.common.dto.PagedResponse;
import com.hotspot.core.dto.social.*;
import com.hotspot.core.dto.user.ReportRequest;
import com.hotspot.core.entity.User;
import com.hotspot.core.service.ThreadInteractionService;
import com.hotspot.core.service.ThreadService;
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
public class ThreadController {

    private final ThreadService threadService;
    private final ThreadInteractionService interactionService;

    @PostMapping("/threads")
    public ResponseEntity<ApiResponse<ThreadResponse>> createThread(@Valid @RequestBody CreateThreadRequest request) {
        User currentUser = AuthenticatedUser.get();
        ThreadResponse response = threadService.createThread(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Thread created", response));
    }

    @GetMapping("/threads/{threadId}")
    public ResponseEntity<ApiResponse<ThreadResponse>> getThread(@PathVariable UUID threadId) {
        ThreadResponse response = threadService.getThread(threadId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/threads/{threadId}")
    public ResponseEntity<ApiResponse<ThreadResponse>> editThread(
            @PathVariable UUID threadId,
            @Valid @RequestBody EditThreadRequest request) {
        User currentUser = AuthenticatedUser.get();
        ThreadResponse response = threadService.editThread(currentUser.getId(), threadId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/threads/{threadId}")
    public ResponseEntity<ApiResponse<Void>> deleteThread(@PathVariable UUID threadId) {
        User currentUser = AuthenticatedUser.get();
        threadService.deleteThread(currentUser.getId(), threadId);
        return ResponseEntity.ok(ApiResponse.success("Thread deleted"));
    }

    @PostMapping("/threads/{threadId}/repost")
    public ResponseEntity<ApiResponse<ThreadResponse>> repostThread(@PathVariable UUID threadId) {
        User currentUser = AuthenticatedUser.get();
        ThreadResponse response = threadService.repostThread(currentUser.getId(), threadId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Thread reposted", response));
    }

    @PostMapping("/threads/{threadId}/quote")
    public ResponseEntity<ApiResponse<ThreadResponse>> quoteThread(
            @PathVariable UUID threadId,
            @Valid @RequestBody QuoteThreadRequest request) {
        User currentUser = AuthenticatedUser.get();
        ThreadResponse response = threadService.quoteThread(currentUser.getId(), threadId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Thread quoted", response));
    }

    @PutMapping("/threads/{threadId}/pin")
    public ResponseEntity<ApiResponse<ThreadResponse>> togglePin(@PathVariable UUID threadId) {
        User currentUser = AuthenticatedUser.get();
        ThreadResponse response = threadService.togglePin(currentUser.getId(), threadId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/threads/{threadId}/like")
    public ResponseEntity<ApiResponse<Void>> likeThread(@PathVariable UUID threadId) {
        User currentUser = AuthenticatedUser.get();
        interactionService.likeThread(currentUser.getId(), threadId);
        return ResponseEntity.ok(ApiResponse.success("Thread liked"));
    }

    @DeleteMapping("/threads/{threadId}/like")
    public ResponseEntity<ApiResponse<Void>> unlikeThread(@PathVariable UUID threadId) {
        User currentUser = AuthenticatedUser.get();
        interactionService.unlikeThread(currentUser.getId(), threadId);
        return ResponseEntity.ok(ApiResponse.success("Thread unliked"));
    }

    @PostMapping("/threads/{threadId}/react")
    public ResponseEntity<ApiResponse<Void>> reactToThread(
            @PathVariable UUID threadId,
            @Valid @RequestBody ReactRequest request) {
        User currentUser = AuthenticatedUser.get();
        interactionService.reactToThread(currentUser.getId(), threadId, request);
        return ResponseEntity.ok(ApiResponse.success("Reaction added"));
    }

    @DeleteMapping("/threads/{threadId}/react")
    public ResponseEntity<ApiResponse<Void>> removeReaction(@PathVariable UUID threadId) {
        User currentUser = AuthenticatedUser.get();
        interactionService.removeReaction(currentUser.getId(), threadId);
        return ResponseEntity.ok(ApiResponse.success("Reaction removed"));
    }

    @PostMapping("/threads/{threadId}/save")
    public ResponseEntity<ApiResponse<Void>> saveThread(@PathVariable UUID threadId) {
        User currentUser = AuthenticatedUser.get();
        interactionService.saveThread(currentUser.getId(), threadId);
        return ResponseEntity.ok(ApiResponse.success("Thread saved"));
    }

    @DeleteMapping("/threads/{threadId}/save")
    public ResponseEntity<ApiResponse<Void>> unsaveThread(@PathVariable UUID threadId) {
        User currentUser = AuthenticatedUser.get();
        interactionService.unsaveThread(currentUser.getId(), threadId);
        return ResponseEntity.ok(ApiResponse.success("Thread unsaved"));
    }

    @PostMapping("/threads/{threadId}/report")
    public ResponseEntity<ApiResponse<Void>> reportThread(
            @PathVariable UUID threadId,
            @Valid @RequestBody ReportRequest request) {
        User currentUser = AuthenticatedUser.get();
        interactionService.reportThread(currentUser.getId(), threadId, request);
        return ResponseEntity.ok(ApiResponse.success("Thread reported"));
    }

    @PostMapping("/threads/{threadId}/mute")
    public ResponseEntity<ApiResponse<Void>> muteThread(@PathVariable UUID threadId) {
        User currentUser = AuthenticatedUser.get();
        interactionService.muteThread(currentUser.getId(), threadId);
        return ResponseEntity.ok(ApiResponse.success("Thread muted"));
    }

    @DeleteMapping("/threads/{threadId}/mute")
    public ResponseEntity<ApiResponse<Void>> unmuteThread(@PathVariable UUID threadId) {
        User currentUser = AuthenticatedUser.get();
        interactionService.unmuteThread(currentUser.getId(), threadId);
        return ResponseEntity.ok(ApiResponse.success("Thread unmuted"));
    }

    @GetMapping("/users/{userId}/threads")
    public ResponseEntity<ApiResponse<PagedResponse<ThreadResponse>>> getUserThreads(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ThreadResponse> threads = threadService.getUserThreads(userId, PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(threads)));
    }
}
