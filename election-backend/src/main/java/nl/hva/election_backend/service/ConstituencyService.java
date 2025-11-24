package nl.hva.election_backend.service;

import nl.hva.election_backend.entity.ConstituencyEntity;
import nl.hva.election_backend.entity.ConstituencyResultEntity;
import nl.hva.election_backend.repository.ConstituencyRepository;
import nl.hva.election_backend.repository.ConstituencyResultRepository;
import org.springframework.stereotype.Service;

@Service
public class ConstituencyService {
    private final ConstituencyRepository constituencyRepository;
    private final ConstituencyResultRepository resultRepository;

    public ConstituencyService(ConstituencyRepository constituencyRepository, ConstituencyResultRepository resultRepository) {
        this.constituencyRepository = constituencyRepository;
        this.resultRepository = resultRepository;
    }

    public ConstituencyEntity saveIfNotExists(String id, int year, String name) {
        if (constituencyRepository.findByNameAndYear(name, year).isPresent()) {
            return null;
        }

        return constituencyRepository.save(new ConstituencyEntity(id, year, name));
    }

    public ConstituencyResultEntity saveResultIfNotExists(int year, String constituencyId, String partyId, int validVotes) {
        if (resultRepository.existsByYearAndConstituencyIdAndPartyId(year, constituencyId, partyId)) {
            return null;
        }

        return resultRepository.save(new ConstituencyResultEntity(year, constituencyId, partyId, validVotes));
    }
}
