package nl.hva.election_backend.dto;

public class VotingGuidePartyDto {
    private final Long id;
    private final String name;

    public VotingGuidePartyDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
