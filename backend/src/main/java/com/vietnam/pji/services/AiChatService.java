package com.vietnam.pji.services;

import com.vietnam.pji.dto.request.CreateChatSessionRequestDTO;
import com.vietnam.pji.dto.request.SendChatMessageRequestDTO;
import com.vietnam.pji.dto.response.PaginationResultDTO;
import com.vietnam.pji.model.agentic.AiChatMessage;
import com.vietnam.pji.model.agentic.AiChatSession;
import org.springframework.data.domain.Pageable;

public interface AiChatService {

    AiChatSession createSession(CreateChatSessionRequestDTO request);

    AiChatMessage sendMessage(Long sessionId, SendChatMessageRequestDTO request);

    PaginationResultDTO getMessages(Long sessionId, Pageable pageable);

    PaginationResultDTO getSessionsByEpisode(Long episodeId, Pageable pageable);
}
