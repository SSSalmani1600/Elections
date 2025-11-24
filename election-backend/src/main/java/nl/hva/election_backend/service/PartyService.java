package nl.hva.election_backend.service;

import nl.hva.election_backend.entity.PartyEntity;
import nl.hva.election_backend.repository.PartyRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PartyService {

    private final PartyRepository partyRepository;

    public PartyService(PartyRepository repository) {
        this.partyRepository = repository;
    }

    public PartyEntity saveIfNotExists(String name, int year, String partyId) {
        return partyRepository
                .findByNameAndYear(name, year)
                .orElseGet(() -> partyRepository.save(new PartyEntity(name, year, partyId)));
    }

    public Set<PartyEntity> getParties() {
        List<PartyEntity> partyList = partyRepository.findAll();
        return new HashSet<>(partyList);
    }
}
