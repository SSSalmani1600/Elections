package nl.hva.election_backend.dto;

import java.time.Instant;
import java.util.UUID;

public class PollOverviewDto {

    private UUID id;
    private String question;
    private Instant createdAt;
    private int eensPercentage;
    private int oneensPercentage;
    private int total;

    public PollOverviewDto(
            UUID id,
            String question,
            Instant createdAt,
            int eensPercentage,
            int oneensPercentage,
            int total
    ) {
        this.id = id;
        this.question = question;
        this.createdAt = createdAt;
        this.eensPercentage = eensPercentage;
        this.oneensPercentage = oneensPercentage;
        this.total = total;
    }

    public UUID getId() { return id; }
    public String getQuestion() { return question; }
    public Instant getCreatedAt() { return createdAt; }
    public int getEensPercentage() { return eensPercentage; }
    public int getOneensPercentage() { return oneensPercentage; }
    public int getTotal() { return total; }
}
