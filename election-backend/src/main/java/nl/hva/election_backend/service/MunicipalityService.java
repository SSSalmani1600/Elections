package nl.hva.election_backend.service;

import nl.hva.election_backend.entity.MunicipalityEntity;
import nl.hva.election_backend.entity.MunicipalityResultEntity;
import nl.hva.election_backend.repository.MunicipalityRepository;
import nl.hva.election_backend.repository.MunicipalityResultRepository;
import org.springframework.stereotype.Service;

@Service
public class MunicipalityService {
    private final MunicipalityRepository municipalityRepo;
    private final MunicipalityResultRepository resultRepo;

    public MunicipalityService(MunicipalityRepository municipalityRepo, MunicipalityResultRepository resultRepo) {
        this.municipalityRepo = municipalityRepo;
        this.resultRepo = resultRepo;
    }

    public MunicipalityEntity saveIfNotExists(int year, String municipalityId, String constituencyId, String name) {
        if (municipalityRepo.existsByYearAndMunicipalityId(year, municipalityId)) return null;

        return municipalityRepo.save(new MunicipalityEntity(year, municipalityId, constituencyId, name));
    }

    public MunicipalityResultEntity saveResultIfNotExists(int year, String municipalityId, String partyId, int validVotes) {
        if (resultRepo.existsByYearAndMunicipalityIdAndPartyId(year, municipalityId, partyId)) return null;

        return resultRepo.save(new MunicipalityResultEntity(year, municipalityId, partyId, validVotes));
    }
}
