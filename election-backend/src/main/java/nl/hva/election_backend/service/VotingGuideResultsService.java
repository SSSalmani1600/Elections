package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.VotingGuideAnswerDto;
import nl.hva.election_backend.dto.VotingGuideRequestDto;
import nl.hva.election_backend.dto.VotingGuideResponseDto;
import nl.hva.election_backend.dto.VotingGuideResultDto;
import nl.hva.election_backend.entity.PartyViewpointEntity;
import nl.hva.election_backend.entity.VotingGuidePartyEntity;
import nl.hva.election_backend.repository.VotingGuidePartyRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class VotingGuideResultsService {
    private final VotingGuidePartyRepository votingGuidePartyRepository;

    public VotingGuideResultsService(VotingGuidePartyRepository votingGuidePartyRepository) {
        this.votingGuidePartyRepository = votingGuidePartyRepository;
    }

    public VotingGuideResponseDto calculate(VotingGuideRequestDto votingGuideAnswers, List<PartyViewpointEntity> partyViewpoints) {
        Set<VotingGuideResultDto> votingGuideResults = new HashSet<>();
        double totalAnswers = votingGuideAnswers.getVotingGuideAnswers().size();
        Map<Long, Map<Long, String>> partyViewpointsMap = new HashMap<>();

//        Converting List to nested Map
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

//        Looping through parties in map, comparing statements and checking if user answer is equals to party position
        for (Map.Entry<Long, Map<Long, String>> entry : partyViewpointsMap.entrySet()) {
            double score = 0;

            for (VotingGuideAnswerDto votingGuideAnswer :  votingGuideAnswers.getVotingGuideAnswers()) {
                Long userStatementId = votingGuideAnswer.getStatementId().longValue();
                String userPosition = votingGuideAnswer.getAnswer();

                Map<Long, String> innerMap = entry.getValue();
                if (innerMap.containsKey(userStatementId)) {
                    if (innerMap.get(userStatementId).equals(userPosition)) {
                        score++;
                    }
                }
            }
            double percentage = (score / totalAnswers) * 100;

            Optional<VotingGuidePartyEntity> party = votingGuidePartyRepository.findById(entry.getKey());
            String partyName = party.isPresent() ? party.get().getName() : "Onbekende partij";

            votingGuideResults.add(new VotingGuideResultDto(entry.getKey(), partyName, percentage));
        }

        return new VotingGuideResponseDto(votingGuideResults);
    }
}
