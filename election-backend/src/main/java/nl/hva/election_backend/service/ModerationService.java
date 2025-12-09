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

    /**
     * Hoofd moderatiemethode — ALLES via Gemini AI.
     */
    public ModerationResult moderateText(String text) {

        long start = System.currentTimeMillis();
        ModerationResult result = new ModerationResult(text);

        ModerationResponse ai = aiModerationClient.analyzeText(text);


        if (ai.isToxic()) {
            result.setBlocked(true);
            result.setFlagged(true);
            result.addWarning("Dit bericht bevat haatdragende of beledigende taal.");
            moderationLogService.logToxicity(text, ai.getToxicityReason());
        }

        if (ai.isMisinformation()) {
            result.setFlagged(true);
            result.setRequiresConfirmation(true);
            result.addWarning("Dit bericht bevat mogelijk misinformatie.");
            moderationLogService.logMisinformation(text, ai.getMisinformationDetails());
        }


        String filtered = text;

        if (ai.getFlaggedWords() != null) {
            for (String word : ai.getFlaggedWords()) {
                filtered = filtered.replaceAll("(?i)" + word, "***");
            }
        }

        result.setModeratedText(filtered);


        long duration = System.currentTimeMillis() - start;
        if (duration > 300) {
            System.err.println("⚠️ Moderatie duurde " + duration + "ms (max 300ms overschreden)");
        }

        return result;
    }


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
