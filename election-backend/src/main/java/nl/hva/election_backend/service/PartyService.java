package nl.hva.election_backend.service;

import nl.hva.election_backend.entity.PartyEntity;
import nl.hva.election_backend.repository.PartyRepository;
import org.springframework.stereotype.Service;

@Service
public class PartyService {

    private final PartyRepository repository;

    public PartyService(PartyRepository repository) {
        this.repository = repository;
    }

    public void saveIfNotExists(String name, int year) {
        repository.findByNameAndYear(name, year)
                .orElseGet(() -> repository.save(new PartyEntity(name, year)));
    }

}
