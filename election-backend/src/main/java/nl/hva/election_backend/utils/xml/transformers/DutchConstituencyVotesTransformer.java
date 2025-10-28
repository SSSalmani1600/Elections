package nl.hva.election_backend.utils.xml.transformers;


import nl.hva.election_backend.model.Candidate;
import nl.hva.election_backend.model.Constituency;
import nl.hva.election_backend.model.Election;
import nl.hva.election_backend.model.Party;
import nl.hva.election_backend.utils.xml.TagAndAttributeNames;
import nl.hva.election_backend.utils.xml.VotesTransformer;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Just prints to content of electionData to the standard output.>br/>
 * <b>This class needs heavy modification!</b>
 */
public class DutchConstituencyVotesTransformer implements VotesTransformer, TagAndAttributeNames {
    private final Election election;
    private String currentAffiliationId = null;
    private String currentPartyName = null;
    private String currentConstituency = null;

    /**
     * Creates a new transformer for handling the votes at the constituency level. It expects an instance of
     * Election that can be used for storing the results.
     * @param election the election in which the votes wil be stored.
     */
    public DutchConstituencyVotesTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
        if (!aggregated) return;

        String affiliationId = electionData.get(AFFILIATION_IDENTIFIER_ID);
        String partyName = electionData.get(REGISTERED_NAME);
        String contestId = electionData.get(CONTEST_IDENTIFIER);

        this.currentAffiliationId = affiliationId;
        this.currentPartyName = partyName;

        election.getParties().stream().filter(party -> Objects.equals(party.getName(), partyName))
                .findFirst()
                .ifPresent(party -> party.setId(affiliationId));

        String constituencyName = electionData.get(CONTEST_NAME);
        this.currentConstituency = constituencyName;
        election.getConstituencies().add(new Constituency(constituencyName, contestId));
        election.getConstituencies().forEach(constituency -> {
            Party party = new Party(partyName);
            party.setId(affiliationId);
            constituency.getParties().add(party);
        });

        int votes = Integer.parseInt(electionData.get(VALID_VOTES));
        election.getConstituencies()
                .stream()
                .filter(constituency -> Objects.equals(constituency.getName(), constituencyName))
                .findFirst().flatMap(constituency -> constituency.getParties()
                        .stream()
                        .filter(party -> Objects.equals(party.getId(), affiliationId))
                        .findFirst()).ifPresent(party -> party.setVotes(votes));
    }

    private Set<Candidate> cloneCandidates(Set<Candidate> src) {
        return src.stream()
                .map(Candidate::new) // uses copy constructor
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
        if (this.currentPartyName == null || this.currentAffiliationId == null) {
            return;
        }

        Set<Candidate> sourceCandidates = election.getParties().stream()
                .filter(p -> Objects.equals(p.getName(), this.currentPartyName))
                .findFirst()
                .map(Party::getCandidates)
                .orElseGet(HashSet::new);

        election.getConstituencies().stream()
                .filter(c -> Objects.equals(c.getName(), currentConstituency))
                .flatMap(c -> c.getParties().stream())
                .filter(p -> Objects.equals(p.getName(), this.currentPartyName)
                        || Objects.equals(p.getId(), this.currentAffiliationId))
                .forEach(p -> {
                    if (p.getCandidates() == null || p.getCandidates().isEmpty()) {
                        p.setCandidates(cloneCandidates(sourceCandidates));
                    }
                });

        // Update the votes for the specific candidate
        String candidateId = electionData.get(CANDIDATE_IDENTIFIER_ID);
        int votes = Integer.parseInt(electionData.get(VALID_VOTES));

        election.getConstituencies().stream()
                .filter(c -> Objects.equals(c.getName(), currentConstituency))
                .flatMap(c -> c.getParties().stream())
                .filter(p -> Objects.equals(p.getId(), currentAffiliationId))
                .flatMap(p -> p.getCandidates().stream())
                .filter(cd -> Objects.equals(cd.getCandidateId(), candidateId))
                .findFirst()
                .ifPresent(cd -> cd.setVotes(votes));
    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        System.out.printf("%s meta data: %s\n", aggregated ? "Constituency" : "Municipality", electionData);
    }
}
