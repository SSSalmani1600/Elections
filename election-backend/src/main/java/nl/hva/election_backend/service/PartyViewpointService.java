package nl.hva.election_backend.service;

import nl.hva.election_backend.entity.PartyViewpointEntity;
import nl.hva.election_backend.repository.PartyViewpointRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class PartyViewpointService {
    private final PartyViewpointRepository partyViewpointRepository;

    public PartyViewpointService(PartyViewpointRepository partyViewpointRepository) {
        this.partyViewpointRepository = partyViewpointRepository;
    }

    public HashSet<PartyViewpointEntity> getAllPartyViewpoints() {
        return partyViewpointRepository.findAll();
    }
}
