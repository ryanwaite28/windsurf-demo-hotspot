package com.hotspot.core.dto.user;

import com.hotspot.core.enums.Visibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePrivacySettingsRequest {
    private Visibility profileVisibility;
    private Visibility postsVisibility;
    private Visibility locationVisibility;
    private Visibility phoneVisibility;
}
