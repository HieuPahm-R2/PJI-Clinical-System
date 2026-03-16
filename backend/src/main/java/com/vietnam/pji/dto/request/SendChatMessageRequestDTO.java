package com.vietnam.pji.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendChatMessageRequestDTO {

    private String content;

    @Builder.Default
    private boolean useEpisodeContext = true;

    @Builder.Default
    private boolean useRunContext = true;

    @Builder.Default
    private boolean useChatHistory = true;
}
