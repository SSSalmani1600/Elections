package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.VotingGuidePartyDto;
import nl.hva.election_backend.entity.VotingGuidePartyEntity;
import nl.hva.election_backend.repository.VotingGuidePartyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VotingGuidePartyService {
    VotingGuidePartyRepository votingGuidePartyRepository;

    public VotingGuidePartyService(VotingGuidePartyRepository votingGuidePartyRepository) {
        this.votingGuidePartyRepository = votingGuidePartyRepository;
    }

    public List<VotingGuidePartyDto> getAllVotingGuideParties() {
        return votingGuidePartyRepository.findAll().stream()
                .map(partyEntity ->
                        new VotingGuidePartyDto(
                                partyEntity.getId(),
                                partyEntity.getName()
                        )).toList();
    }
}
