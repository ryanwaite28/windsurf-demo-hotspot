package com.hotspot.core.dto.social;

import com.hotspot.core.enums.ThreadType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ThreadResponse {
    private String id;
    private AuthorInfo author;
    private String content;
    private ThreadType threadType;
    private String parentThreadId;
    private boolean pinned;
    private long likesCount;
    private long reactionsCount;
    private long commentsCount;
    private List<MediaResponse> media;
    private PollResponse poll;
    private Instant createdAt;
    private Instant updatedAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorInfo {
        private String id;
        private String username;
        private String displayName;
        private String profilePictureUrl;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MediaResponse {
        private String id;
        private String mediaType;
        private String url;
        private int displayOrder;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PollResponse {
        private String id;
        private String question;
        private List<PollOptionResponse> options;
        private Instant expiresAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PollOptionResponse {
        private String id;
        private String optionText;
        private long voteCount;
        private int displayOrder;
    }
}
