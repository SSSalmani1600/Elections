package nl.hva.election_backend.dto;

import java.util.Set;

public class VotingGuideRequestDto {
    private Set<VotingGuideAnswerDto> votingGuideAnswers;

    public VotingGuideRequestDto(Set<VotingGuideAnswerDto> votingGuideAnswers) {
        this.votingGuideAnswers = votingGuideAnswers;
    }

    public Set<VotingGuideAnswerDto> getVotingGuideAnswers() {
        return votingGuideAnswers;
    }

    public void setVotingGuideAnswers(Set<VotingGuideAnswerDto> votingGuideAnswers) {
        this.votingGuideAnswers = votingGuideAnswers;
    }
}
