
package nl.hva.election_backend.dto;

import java.time.Instant;
import java.util.List;

// dit is data van discussie, wordt naar frontend gestuurd
public record DiscussionDetailDto(
        String id,
        String title,
        String author,
        String body,
        Instant createdAt,
        Instant lastActivityAt,
        int reactionsCount,
        List<ReactionDto> reactions
) {}
