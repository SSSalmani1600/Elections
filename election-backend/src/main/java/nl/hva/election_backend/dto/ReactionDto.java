package nl.hva.election_backend.dto;

import java.time.Instant;

public record ReactionDto(
        String author,
        String message,
        Instant createdAt
) {}
