package nl.hva.election_backend.dto;


import java.time.Instant;

// dit is data van discussie, wordt naar frontend gestuurd
public record DiscussionListItemDto(
        String id,
        String title,
        String author,
        Instant lastActivityAt,
        int reactionsCount
) {}
