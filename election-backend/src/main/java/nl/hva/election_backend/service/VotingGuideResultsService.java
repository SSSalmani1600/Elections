package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.VotingGuideAnswerDto;
import nl.hva.election_backend.dto.VotingGuideRequestDto;
import nl.hva.election_backend.dto.VotingGuideResponseDto;
import nl.hva.election_backend.dto.VotingGuideResultDto;
import nl.hva.election_backend.entity.PartyViewpointEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VotingGuideResultsService {
    public VotingGuideResponseDto calculate(VotingGuideRequestDto votingGuideAnswers, List<PartyViewpointEntity> partyViewpoints) {
        Set<VotingGuideResultDto> votingGuideResults = new HashSet<>();
        double totalAnswers = votingGuideAnswers.getVotingGuideAnswers().size();
        Map<Long, Map<Long, String>> partyViewpointsMap = new HashMap<>();

        partyViewpoints.forEach(partyViewpoint -> {
            if (!partyViewpointsMap.containsKey(partyViewpoint.getPartyId())) {
                partyViewpointsMap.put(partyViewpoint.getPartyId(), new HashMap<>());
                Map<Long, String> innerMap =  partyViewpointsMap.get(partyViewpoint.getPartyId());
                innerMap.put(partyViewpoint.getStatementId(), partyViewpoint.getPosition());
            } else {
                Map<Long, String> innerMap =  partyViewpointsMap.get(partyViewpoint.getPartyId());
                innerMap.put(partyViewpoint.getStatementId(), partyViewpoint.getPosition());
            }
        });
    }
}
