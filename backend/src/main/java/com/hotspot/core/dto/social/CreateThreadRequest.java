package com.hotspot.core.dto.social;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateThreadRequest {

    @Size(max = 5000, message = "Thread content must be at most 5000 characters")
    private String content;

    private List<MediaItem> media;

    private PollRequest poll;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MediaItem {
        private String mediaType;
        private String url;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PollRequest {
        private String question;
        private List<String> options;
        private Long expiresInHours;
    }
}
