package nl.hva.election_backend.service.ai;// nl.hva.election_backend.service.ai.AiModerationClient

import nl.hva.election_backend.dto.ModerationResponse;

public interface AiModerationClient {

    /**
     * Analyseert de tekst op scheldwoorden/toxiciteit en mogelijke misinformatie.
     * @param text Het te analyseren bericht.
     * @return Een DTO met de analyse resultaten.
     */
    ModerationResponse analyzeText(String text);
}
