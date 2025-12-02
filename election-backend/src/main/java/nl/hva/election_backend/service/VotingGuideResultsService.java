package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.VotingGuideRequestDto;
import nl.hva.election_backend.dto.VotingGuideResponseDto;
import nl.hva.election_backend.dto.VotingGuideResultDto;
import nl.hva.election_backend.entity.PartyViewpointEntity;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VotingGuideResultsService {
    public VotingGuideResponseDto calculate(VotingGuideRequestDto votingGuideAnswers, List<PartyViewpointEntity> partyViewpoints) {
        Set<VotingGuideResultDto> votingGuideResults = new HashSet<>();

        for (PartyViewpointEntity partyViewpointEntity : partyViewpoints) {

        }
    }
}
