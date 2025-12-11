package nl.hva.election_backend.service.ai;// nl.hva.election_backend.service.ai.AiModerationClient

import nl.hva.election_backend.dto.ModerationResponse;

public interface AiModerationClient {


    ModerationResponse analyzeText(String text);
}
