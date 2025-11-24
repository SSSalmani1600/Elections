package nl.hva.election_backend.utils.xml.transformers;


import io.micrometer.common.util.StringUtils;
import nl.hva.election_backend.entity.ConstituencyEntity;
import nl.hva.election_backend.entity.ConstituencyResultEntity;
import nl.hva.election_backend.model.Candidate;
import nl.hva.election_backend.model.Constituency;
import nl.hva.election_backend.model.Election;
import nl.hva.election_backend.model.Party;
import nl.hva.election_backend.repository.ConstituencyRepository;
import nl.hva.election_backend.service.ConstituencyService;
import nl.hva.election_backend.utils.xml.TagAndAttributeNames;
import nl.hva.election_backend.utils.xml.VotesTransformer;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Just prints to content of electionData to the standard output.>br/>
 * <b>This class needs heavy modification!</b>
 */
public class DutchConstituencyVotesTransformer implements VotesTransformer, TagAndAttributeNames {
    private final Election election;
    private final ConstituencyService constituencyService;
    private String currentContestId;

    /**
     * Creates a new transformer for handling the votes at the constituency level. It expects an instance of
     * Election that can be used for storing the results.
     *
     * @param election the election in which the votes wil be stored.
     */
    public DutchConstituencyVotesTransformer(Election election, ConstituencyService constituencyService) {
        this.election = election;
        this.constituencyService = constituencyService;
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
        if (!aggregated) return;

        String contestId = electionData.get(CONTEST_IDENTIFIER_ID);
        String constituencyName = electionData.get(CONTEST_NAME);
        String electionYear = electionData.get(ELECTION_DATE).split("-")[0];

        String partyId = electionData.get(AFFILIATION_IDENTIFIER_ID);
        String validVotes = electionData.get(VALID_VOTES);

        ConstituencyEntity savedConstituency = null;
        ConstituencyResultEntity savedResult = null;
            
        if (StringUtils.isNotBlank(constituencyName) &&
                StringUtils.isNotBlank(contestId) &&
                StringUtils.isNotBlank(electionYear) &&
                StringUtils.isNotBlank(partyId)) {

            // Save constituency
            if (!Objects.equals(contestId, currentContestId)) {
                savedConstituency = constituencyService.saveIfNotExists(contestId, Integer.parseInt(electionYear), constituencyName);
            }

            // Save total votes of each party in constituency
            savedResult = constituencyService
                    .saveResultIfNotExists(Integer.parseInt(electionYear), contestId, partyId, Integer.parseInt(validVotes));

            currentContestId = contestId;
        }

        if (savedConstituency == null) {
            System.out.println("constituency exists in db");
        } else {
            System.out.println("Constituency saved: " + savedConstituency.getYear() + " - " + savedConstituency.getConstituencyId());
        }

        if (savedResult == null) {
            System.out.println("result exists in db");
        } else {
            System.out.println("Result saved: " + savedResult.getYear() + " - " + savedResult.getConstituencyId());
        }
    }

    private Set<Candidate> cloneCandidates(Set<Candidate> src) {
        return src.stream()
                .map(Candidate::new) // uses copy constructor
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
//        if (this.currentPartyName == null || this.currentAffiliationId == null) {
//            return;
//        }
//
//        Set<Candidate> sourceCandidates = election.getParties().stream()
//                .filter(p -> Objects.equals(p.getName(), this.currentPartyName))
//                .findFirst()
//                .map(Party::getCandidates)
//                .orElseGet(HashSet::new);
//
//        election.getConstituencies().stream()
//                .filter(c -> Objects.equals(c.getName(), currentConstituency))
//                .flatMap(c -> c.getParties().stream())
//                .filter(p -> Objects.equals(p.getName(), this.currentPartyName)
//                        || Objects.equals(p.getId(), this.currentAffiliationId))
//                .forEach(p -> {
//                    if (p.getCandidates() == null || p.getCandidates().isEmpty()) {
//                        p.setCandidates(cloneCandidates(sourceCandidates));
//                    }
//                });
//
//        // Update the votes for the specific candidate
//        String candidateId = electionData.get(CANDIDATE_IDENTIFIER_ID);
//        int votes = Integer.parseInt(electionData.get(VALID_VOTES));
//
//        election.getConstituencies().stream()
//                .filter(c -> Objects.equals(c.getName(), currentConstituency))
//                .flatMap(c -> c.getParties().stream())
//                .filter(p -> Objects.equals(p.getId(), currentAffiliationId))
//                .flatMap(p -> p.getCandidates().stream())
//                .filter(cd -> Objects.equals(cd.getCandidateId(), candidateId))
//                .findFirst()
//                .ifPresent(cd -> cd.setVotes(votes));
    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        System.out.printf("%s meta data: %s\n", aggregated ? "Constituency" : "Municipality", electionData);
    }
}
