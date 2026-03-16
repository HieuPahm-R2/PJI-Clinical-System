package com.vietnam.pji.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class ImageResultRequestDTO {

    @NotNull(message = "episodeId must not be null")
    private Long episodeId;

    private String type;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate imagingDate;

    private String findings;

    private String fileMetadata;
}
