package nl.hva.election_backend.service;

import nl.hva.election_backend.entity.ConstituencyEntity;
import nl.hva.election_backend.repository.ConstituencyRepository;
import org.springframework.stereotype.Service;

@Service
public class ConstituencyService {
    private final ConstituencyRepository constituencyRepository;

    public ConstituencyService(ConstituencyRepository constituencyRepository) {
        this.constituencyRepository = constituencyRepository;
    }

    public ConstituencyEntity saveIfNotExists(String id, int year, String name) {
        if (constituencyRepository.findByNameAndYear(name, year).isPresent()) {
            return null;
        }

        return constituencyRepository.save(new ConstituencyEntity(id, year, name));
    }
}
