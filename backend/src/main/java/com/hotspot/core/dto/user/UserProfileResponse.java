package com.hotspot.core.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String id;
    private String email;
    private String username;
    private String displayName;
    private String bio;
    private String profilePictureUrl;
    private String location;
    private String phone;
    private boolean phoneVerified;
    private long followersCount;
    private long followingCount;
    private Instant createdAt;
}
