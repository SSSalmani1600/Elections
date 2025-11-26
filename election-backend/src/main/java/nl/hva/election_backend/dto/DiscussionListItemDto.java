package nl.hva.election_backend.dto;

import java.time.Instant;

// Lijstweergave van discussies
public record DiscussionListItemDto(
        String id,
        String title,
        String author,      // ✔ username van UserEntity
        Instant lastActivityAt,
        int reactionsCount
) {}
