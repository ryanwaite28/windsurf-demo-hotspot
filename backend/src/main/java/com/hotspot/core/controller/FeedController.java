package com.hotspot.core.controller;

import com.hotspot.common.dto.ApiResponse;
import com.hotspot.common.dto.PagedResponse;
import com.hotspot.core.dto.social.ThreadResponse;
import com.hotspot.core.entity.User;
import com.hotspot.core.service.FeedService;
import com.hotspot.security.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/social")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<PagedResponse<ThreadResponse>>> getFeed(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User currentUser = AuthenticatedUser.get();
        Page<ThreadResponse> feed = feedService.getFeed(currentUser.getId(), PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(PagedResponse.from(feed)));
    }
}
