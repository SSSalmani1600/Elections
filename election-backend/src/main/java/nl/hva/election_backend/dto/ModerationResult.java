package nl.hva.election_backend.dto;

import java.util.ArrayList;
import java.util.List;

public class ModerationResult {
    private String originalText;
    private String moderatedText; // De gefilterde tekst (met ***)
    private boolean isFlagged = false;
    private boolean isBlocked = false; // Automatisch blokkeren (bv. bij zware toxiciteit)
    private boolean requiresConfirmation = false; // Voor misinformatie
    private final List<String> warnings = new ArrayList<>(); // Meldingen voor de gebruiker

    /**
     * @param originalText De tekst die gemodereerd wordt.
     */
    public ModerationResult(String originalText) {
        this.originalText = originalText;
        this.moderatedText = originalText; // Initieel is de gemodereerde tekst hetzelfde
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
        return isFlagged;
    }

    public void setFlagged(boolean flagged) {
        isFlagged = flagged;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
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
}