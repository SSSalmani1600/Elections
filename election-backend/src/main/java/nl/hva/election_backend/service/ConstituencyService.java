package nl.hva.election_backend.service;

import nl.hva.election_backend.entity.ConstituencyEntity;
import nl.hva.election_backend.entity.ConstituencyResultEntity;
import nl.hva.election_backend.model.Constituency;
import nl.hva.election_backend.repository.ConstituencyRepository;
import nl.hva.election_backend.repository.ConstituencyResultRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

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

    private Constituency toConstituency(ConstituencyEntity entity) {
        Constituency constituency = new Constituency();
        constituency.setConstituencyId(entity.getConstituencyId());
        constituency.setName(entity.getName());
        return constituency;
    }

    public Set<Constituency> getConstituencies(int electionId) {
        return constituencyRepository.findAllByYear(electionId)
                .stream().map(this::toConstituency).collect(Collectors.toSet());
    }
}
