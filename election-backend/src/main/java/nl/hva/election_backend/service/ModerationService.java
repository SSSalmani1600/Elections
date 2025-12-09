package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.ModerationResult;
import nl.hva.election_backend.dto.ModerationResponse;
import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.repository.ReactionRepository;
import nl.hva.election_backend.service.ai.AiModerationClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModerationService {

    private final ReactionRepository reactionRepository;
    private final AiModerationClient aiModerationClient;
    private final ModerationLogService moderationLogService;

    public ModerationService(
            ReactionRepository reactionRepository,
            AiModerationClient aiModerationClient,
            ModerationLogService moderationLogService
    ) {
        this.reactionRepository = reactionRepository;
        this.aiModerationClient = aiModerationClient;
        this.moderationLogService = moderationLogService;
    }

    public ModerationResult moderateText(String text) {

        ModerationResult result = new ModerationResult(text);
        ModerationResponse aiResponse = null;

        try {
            aiResponse = aiModerationClient.analyzeText(text);
        } catch (Exception e) {
            System.err.println("❌ AI moderatie error: " + e.getMessage());
        }

        // AI faalt → fallback
        if (aiResponse == null) {
            System.err.println("⚠️ Gemini faalde → FALLBACK ACTIVE");
            return applyFallbackModeration(text, new ModerationResult(text));
        }

        // AI Resultaten verwerken
        if (aiResponse.isToxic()) {
            result.setBlocked(true);
            result.setFlagged(true);
            result.setModerationStatus("BLOCKED");
            result.addWarning("Dit bericht bevat haatdragende of beledigende taal.");
            moderationLogService.logToxicity(text, aiResponse.getToxicityReason());
        }

        if (aiResponse.isMisinformation()) {
            result.setFlagged(true);
            result.setRequiresConfirmation(true);
            result.setModerationStatus("FLAGGED");
            result.addWarning("Dit bericht bevat mogelijk misinformatie.");
            moderationLogService.logMisinformation(text, aiResponse.getMisinformationDetails());
        }

        // Scheldwoorden maskeren
        String filtered = text;
        if (aiResponse.getFlaggedWords() != null) {
            for (String word : aiResponse.getFlaggedWords()) {
                filtered = filtered.replaceAll("(?i)" + word, "***");
            }
        }

        result.setModeratedText(filtered);

        // Als niets flagged of blocked → PENDING
        if (!result.isFlagged() && !result.isBlocked()) {
            result.setModerationStatus("PENDING");
        }

        return result;
    }


    // -------------------------------
    // FALLBACK VOOR ALS GEMINI FAALT
    // -------------------------------

    private static final String[] BANNED_WORDS = {
            "kanker", "kkr", "tyfus", "mongool", "homo", "neger"
    };

    private ModerationResult applyFallbackModeration(String text, ModerationResult result) {

        String filtered = text;
        boolean containsBadWord = false;

        for (String word : BANNED_WORDS) {
            if (text.toLowerCase().contains(word)) {
                filtered = filtered.replaceAll("(?i)" + word, "***");
                result.setBlocked(true);
                result.setFlagged(true);
                result.addWarning("Bevat verboden taal: " + word);
                containsBadWord = true;
            }
        }

        // GEEN scheldwoorden → PENDING
        if (!containsBadWord) {
            result.setModerationStatus("PENDING");
            result.setModeratedText(text);
            return result;
        }

        // WEL scheldwoorden → BLOCKED
        result.setModerationStatus("BLOCKED");
        result.setModeratedText(filtered);
        return result;
    }


    // -------------------------------
    // ADMIN API METHODS
    // -------------------------------
    public List<ReactionEntity> getPendingReactions() {
        return reactionRepository.findByModerationStatus("PENDING");
    }

    public List<ReactionEntity> getFlaggedReactions() {
        return reactionRepository.findByModerationStatus("FLAGGED");
    }

    public void approveReaction(Long id) {
        ReactionEntity reaction = reactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reaction not found: " + id));

        reaction.setModerationStatus("APPROVED");
        reactionRepository.save(reaction);
    }

    public void rejectReaction(Long id) {
        ReactionEntity reaction = reactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reaction not found: " + id));

        reaction.setModerationStatus("REJECTED");
        reactionRepository.save(reaction);
    }

    public void flagReaction(Long id, String reason) {
        ReactionEntity reaction = reactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reaction not found: " + id));

        reaction.setModerationStatus("FLAGGED");
        reaction.setFlaggedReason(reason);
        reactionRepository.save(reaction);
    }
}
