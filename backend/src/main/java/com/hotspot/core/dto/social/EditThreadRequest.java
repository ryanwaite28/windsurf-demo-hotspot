package com.hotspot.core.dto.social;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EditThreadRequest {

    @NotBlank(message = "Content is required")
    @Size(max = 5000, message = "Thread content must be at most 5000 characters")
    private String content;
}
