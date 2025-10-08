package nl.hva.election_backend.discussion;

import java.time.Instant;

public record DiscussionListItemDto(
        String id,
        String title,
        String author,
        Instant lastActivityAt,
        int reactionsCount
) {}
