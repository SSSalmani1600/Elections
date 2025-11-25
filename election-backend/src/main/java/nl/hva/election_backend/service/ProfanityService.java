package nl.hva.election_backend.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProfanityService {


    private final List<String> bannedWords = Arrays.asList(
            "kanker", "kut", "tering", "hoer", "fuck", "sukkel", "idioot", "mongool"
    );


    public boolean containsProfanity(String text) {
        if (text == null) return false;
        String lower = text.toLowerCase();

        return bannedWords.stream().anyMatch(lower::contains);
    }


    public String findProfanity(String text) {
        if (text == null) return null;
        String lower = text.toLowerCase();

        return bannedWords.stream()
                .filter(lower::contains)
                .findFirst()
                .orElse(null);
    }
}
