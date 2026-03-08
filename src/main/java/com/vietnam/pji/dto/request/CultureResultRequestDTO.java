package com.vietnam.pji.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CultureResultRequestDTO {

    @NotNull(message = "episodeId must not be null")
    private Long episodeId;

    private String sampleType;

    private Integer incubationDays;

    private String organismName;

    private String result;

    private String gramType;

    private String notes;
}
