package com.vietnam.pji.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class DoctorRecommendationReviewRequestDTO {

    @NotNull(message = "runId must not be null")
    private Long runId;

    @NotBlank(message = "reviewStatus must not be blank")
    private String reviewStatus;

    private String reviewNote;

    private Map<String, Object> modificationJson;

    private String rejectionReason;
}
