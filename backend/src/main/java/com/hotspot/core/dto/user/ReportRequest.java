package com.hotspot.core.dto.user;

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
public class ReportRequest {

    @NotBlank(message = "Reason is required")
    @Size(max = 100, message = "Reason must be at most 100 characters")
    private String reason;

    @Size(max = 1000, message = "Details must be at most 1000 characters")
    private String details;
}
