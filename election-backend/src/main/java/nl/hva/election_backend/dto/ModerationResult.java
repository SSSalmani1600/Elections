package nl.hva.election_backend.dto;

import java.util.ArrayList;
import java.util.List;

public class ModerationResult {
    private String originalText;
    private String moderatedText;

    private boolean flagged = false;
    private boolean blocked = false;
    private boolean requiresConfirmation = false;

    private String moderationStatus = "PENDING";
    private final List<String> warnings = new ArrayList<>();

    public ModerationResult(String originalText,
                            String moderationStatus,
                            boolean flagged,
                            boolean blocked,
                            List<String> warnings) {

        this.originalText = originalText;
        this.moderatedText = originalText; // default: geen censoring

        this.moderationStatus = moderationStatus;
        this.flagged = flagged;
        this.blocked = blocked;

        if (warnings != null) {
            this.warnings.addAll(warnings);
        }
    }

    public ModerationResult(String text) {
    }


    public String getOriginalText() {
        return originalText;
    }

    public String getModeratedText() {
        return moderatedText;
    }

    public void setModeratedText(String moderatedText) {
        this.moderatedText = moderatedText;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isRequiresConfirmation() {
        return requiresConfirmation;
    }

    public void setRequiresConfirmation(boolean requiresConfirmation) {
        this.requiresConfirmation = requiresConfirmation;
    }

    public List<String> getWarnings() {
        return warnings;
    }

    public void addWarning(String warning) {
        this.warnings.add(warning);
    }


    public String getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(String moderationStatus) {
        this.moderationStatus = moderationStatus;
    }
}
