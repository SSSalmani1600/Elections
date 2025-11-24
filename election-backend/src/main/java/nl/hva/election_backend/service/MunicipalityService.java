package nl.hva.election_backend.service;

import nl.hva.election_backend.entity.MunicipalityEntity;
import nl.hva.election_backend.repository.MunicipalityRepository;

public class MunicipalityService {
    private final MunicipalityRepository municipalityRepo;

    public MunicipalityService(MunicipalityRepository municipalityRepo) {
        this.municipalityRepo = municipalityRepo;
    }

    public MunicipalityEntity saveIfNotExists(int year, String municipalityId, String constituencyId, String name) {
        if (municipalityRepo.existsByYearAndMunicipalityId(year, municipalityId)) return null;

        return municipalityRepo.save(new MunicipalityEntity(year, municipalityId, constituencyId, name));
    }
}
