package nl.hva.election_backend.service;

import org.springframework.stereotype.Service;

@Service
public class ModerationLogService {


    public void logProfanity(String fullText, String foundWord) {
        System.out.println("[MOD-LOG - PROFANITY] Gevonden woord: '" + foundWord + "'. Actie: FILTERED.");

    }

    public void logToxicity(String fullText, String reason) {
        System.out.println("[MOD-LOG - TOXICITY] Toxiciteit gedetecteerd. Reden: " + reason + ". Actie: BLOCKED.");

    }

    public void logMisinformation(String fullText, String claim) {

        System.out.println("[MOD-LOG - MISINFO] Potentiële misinformatie gemarkeerd. Claim: " + claim + ". Actie: CONFIRMATION REQUIRED.");
    }
}