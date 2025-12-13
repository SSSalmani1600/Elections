package nl.hva.election_backend.dto;

import java.time.Instant;
import java.util.List;

// DTO (Data Transfer Object) voor de detailweergave van 1 discussie
// Dit object wordt naar de frontend gestuurd met alle details van een discussie
// Record class: automatisch getters, equals, hashCode, etc.
public record DiscussionDetailDto(
        String id,                      // Unieke ID van de discussie
        Long userId,                    // ID van de auteur (voor eigenaar check)
        String title,                   // Titel van de discussie
        String author,                  // Username van de auteur
        String body,                    // Volledige inhoud van de discussie
        Instant createdAt,              // Wanneer de discussie is aangemaakt
        Instant lastActivityAt,         // Wanneer er voor het laatst activiteit was
        int reactionsCount,             // Totaal aantal reacties
        List<ReactionDto> reactions     // Lijst van alle reacties op deze discussie
) {}
