package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.MunicipalityDto;
import nl.hva.election_backend.dto.PartyResultDto;
import nl.hva.election_backend.entity.MunicipalityEntity;
import nl.hva.election_backend.entity.MunicipalityResultEntity;
import nl.hva.election_backend.repository.MunicipalityRepository;
import nl.hva.election_backend.repository.MunicipalityResultRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    public MunicipalityDto getMunicipalityData(Integer year, String name) {
        // Fetch data
        MunicipalityEntity entity = municipalityRepo.findWithResultsByYearsAndName(year, name)
                .orElseThrow(() -> new RuntimeException("Municipality not found"));

        // Map to DTO
        MunicipalityDto dto = new MunicipalityDto();
        dto.setMunicipalityId(entity.getMunicipalityId());
        dto.setName(entity.getName());
        dto.setConstituencyId(entity.getConstituencyId());

        // Map the results list
        List<PartyResultDto> parties = entity.getResults().stream()
                .map(r -> new PartyResultDto(
                        r.getPartyId(),
                        r.getParty().getName(),
                        r.getValidVotes()
                )).toList();

        dto.setParties(parties);

        return dto;
    }
}
