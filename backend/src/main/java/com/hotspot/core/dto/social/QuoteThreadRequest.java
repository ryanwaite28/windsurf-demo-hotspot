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
public class QuoteThreadRequest {

    @NotBlank(message = "Content is required for a quote")
    @Size(max = 5000, message = "Quote content must be at most 5000 characters")
    private String content;
}
