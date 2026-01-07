package nl.hva.election_backend.dto;

import java.time.Instant;
import java.util.UUID;

public record PollOverviewDto(
        UUID id,
        String question,
        Instant createdAt,
        int eensPercentage,
        int oneensPercentage,
        int total
) {}
