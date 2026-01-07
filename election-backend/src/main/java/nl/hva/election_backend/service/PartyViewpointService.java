package nl.hva.election_backend.service;

import nl.hva.election_backend.entity.PartyViewpointEntity;
import nl.hva.election_backend.repository.PartyViewpointRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class PartyViewpointService {
    private final PartyViewpointRepository partyViewpointRepository;

    public PartyViewpointService(PartyViewpointRepository partyViewpointRepository) {
        this.partyViewpointRepository = partyViewpointRepository;
    }

    public List<PartyViewpointEntity> getAllPartyViewpoints() {
        return partyViewpointRepository.findAll();
    }
}