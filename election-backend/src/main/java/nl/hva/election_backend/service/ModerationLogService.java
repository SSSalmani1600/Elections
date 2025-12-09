package nl.hva.election_backend.service;

import org.springframework.stereotype.Service;

@Service
public class ModerationLogService {

    /**
     * @param fullText De complete tekst (wordt niet gelogd, kan eventueel gebruikt worden voor contextuele hashing).
     * @param foundWord Het specifiek gevonden scheldwoord.
     */
    public void logProfanity(String fullText, String foundWord) {
        System.out.println("[MOD-LOG - PROFANITY] Gevonden woord: '" + foundWord + "'. Actie: FILTERED.");

    }

    /**
     * @param fullText De complete tekst.
     * @param reason De reden of categorie van toxiciteit (bijv. "HATE_SPEECH").
     */
    public void logToxicity(String fullText, String reason) {
        System.out.println("[MOD-LOG - TOXICITY] Toxiciteit gedetecteerd. Reden: " + reason + ". Actie: BLOCKED.");

    }

    /**
     * @param fullText De complete tekst.
     * @param claim De specifieke claim of reden waarom het gemarkeerd werd.
     */
    public void logMisinformation(String fullText, String claim) {

        System.out.println("[MOD-LOG - MISINFO] Potentiële misinformatie gemarkeerd. Claim: " + claim + ". Actie: CONFIRMATION REQUIRED.");
    }
}