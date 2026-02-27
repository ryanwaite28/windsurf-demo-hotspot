package com.hotspot.core.dto.social;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private String id;
    private String threadId;
    private ThreadResponse.AuthorInfo author;
    private String content;
    private Instant createdAt;
    private Instant updatedAt;
}
