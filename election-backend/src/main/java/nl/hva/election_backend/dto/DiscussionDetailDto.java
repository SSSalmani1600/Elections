package nl.hva.election_backend.dto;

import java.time.Instant;
import java.util.List;

// Detailweergave van 1 discussie
public record DiscussionDetailDto(
        String id,
        String title,
        String author,              // ✔ username van UserEntity
        String body,
        Instant createdAt,
        Instant lastActivityAt,
        int reactionsCount,
        List<ReactionDto> reactions
) {}
