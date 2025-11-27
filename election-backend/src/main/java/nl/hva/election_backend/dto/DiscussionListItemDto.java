package nl.hva.election_backend.dto;

import java.time.Instant;

// DTO voor de lijstweergave van discussies
// Dit object wordt gebruikt op de overzichtspagina waar alle discussies worden getoond
// Bevat alleen de belangrijkste info (geen volledige body)
public record DiscussionListItemDto(
        String id,                      // Unieke ID van de discussie
        String title,                   // Titel van de discussie
        String author,                  // Username van de auteur
        Instant lastActivityAt,         // Wanneer er voor het laatst activiteit was (voor sorteren)
        int reactionsCount              // Aantal reacties (voor weergave)
) {}
