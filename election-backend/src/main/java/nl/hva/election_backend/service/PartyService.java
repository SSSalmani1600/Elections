package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.Candidate;
import nl.hva.election_backend.dto.PartyDetail;
import nl.hva.election_backend.entity.CandidateEntity;
import nl.hva.election_backend.entity.PartyEntity;
import nl.hva.election_backend.repository.CandidateRepository;
import nl.hva.election_backend.repository.PartyRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class PartyService {

    private final PartyRepository partyRepository;
    private final CandidateRepository candidateRepository;

    public PartyService(PartyRepository partyRepository, CandidateRepository candidateRepository) {
        this.partyRepository = partyRepository;
        this.candidateRepository = candidateRepository;
    }

    public PartyEntity saveIfNotExists(String name, int year, String partyId) {
        return partyRepository
                .findByNameAndYear(name, year)
                .orElseGet(() -> partyRepository.save(new PartyEntity(name, year, partyId)));
    }

    public Set<PartyEntity> getParties() {
        return new HashSet<>(partyRepository.findAll());
    }

    public PartyDetail getPartyDetail(String partyName) {

        PartyEntity newest = partyRepository
                .findTopByNameIgnoreCaseOrderByYearDesc(partyName)
                .orElseThrow(() ->
                        new RuntimeException("Partij niet gevonden: " + partyName)
                );

        String partyId = newest.getPartyId();

        List<CandidateEntity> candidates =
                candidateRepository.findByParty_PartyIdAndYearOrderByCandidateIdAsc(
                        partyId,
                        newest.getYear()
                );

        PartyDetail dto = new PartyDetail();
        dto.partyId = partyId;
        dto.name = newest.getName();
        dto.year = newest.getYear();
        dto.candidates = candidates.stream()
                .map(this::mapCandidate)
                .toList();

        return dto;
    }

    private Candidate mapCandidate(CandidateEntity c) {
        Candidate dto = new Candidate();
        dto.candidateId = c.getCandidateId();
        dto.firstName = c.getFirstName();
        dto.namePrefix = c.getNamePrefix();
        dto.lastName = c.getLastName();
        dto.gender = c.getGender();
        return dto;
    }
}
