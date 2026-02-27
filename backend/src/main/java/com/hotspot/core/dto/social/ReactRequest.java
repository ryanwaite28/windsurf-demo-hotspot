package com.hotspot.core.dto.social;

import com.hotspot.core.enums.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReactRequest {

    @NotNull(message = "Reaction type is required")
    private ReactionType reactionType;
}
