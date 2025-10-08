package nl.hva.election_backend.discussion;

import java.time.Instant;

public record DiscussionDetailDto(
        String id,
        String title,
        String author,
        String body,
        Instant createdAt,
        Instant lastActivityAt,
        int reactionsCount
) {}
