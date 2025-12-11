package nl.hva.election_backend.dto;

import java.util.Set;

public class VotingGuideResponseDto {
   private Set<VotingGuideResultDto> votingGuideResults;

    public VotingGuideResponseDto(Set<VotingGuideResultDto> votingGuideResults) {
        this.votingGuideResults = votingGuideResults;
    }

    public Set<VotingGuideResultDto> getVotingGuideResults() {
        return votingGuideResults;
    }

    public void setVotingGuideResults(Set<VotingGuideResultDto> votingGuideResults) {
        this.votingGuideResults = votingGuideResults;
    }
}
