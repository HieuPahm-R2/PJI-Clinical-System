package com.vietnam.pji.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatSessionRequestDTO {

    private Long episodeId;
    private Long runId;
    private Long currentItemId;
    private String chatType;
    private String title;
}
