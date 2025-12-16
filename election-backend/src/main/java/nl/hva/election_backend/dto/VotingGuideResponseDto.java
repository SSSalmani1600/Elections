package nl.hva.election_backend.dto;

import java.util.List;
import java.util.Set;

public class VotingGuideResponseDto {
   private List<VotingGuideResultDto> votingGuideResults;

    public VotingGuideResponseDto(List<VotingGuideResultDto> votingGuideResults) {
        this.votingGuideResults = votingGuideResults;
    }

    public List<VotingGuideResultDto> getVotingGuideResults() {
        return votingGuideResults;
    }

    public void setVotingGuideResults(List<VotingGuideResultDto> votingGuideResults) {
        this.votingGuideResults = votingGuideResults;
    }
}
