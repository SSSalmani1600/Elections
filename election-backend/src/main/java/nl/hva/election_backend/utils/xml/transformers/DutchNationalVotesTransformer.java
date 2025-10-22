package nl.hva.election_backend.utils.xml.transformers;


import nl.hva.election_backend.model.Election;
import nl.hva.election_backend.utils.xml.VotesTransformer;

import java.util.Map;
import java.util.Objects;

import static nl.hva.election_backend.utils.xml.TagAndAttributeNames.*;

/**
 * Just prints to content of electionData to the standard output.>br/>
 * <b>This class needs heavy modification!</b>
 */
public class DutchNationalVotesTransformer implements VotesTransformer {
    private final Election election;
    private String affiliationId;

    /**
     * Creates a new transformer for handling the votes at the national level. It expects an instance of
     * Election that can be used for storing the results.
     * @param election the election in which the votes wil be stored.
     */
    public DutchNationalVotesTransformer(Election election) { this.election = election; }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> electionData) {
        if (!aggregated) return;
        String affiliationId = electionData.get(AFFILIATION_IDENTIFIER_ID);
        String registerName = electionData.get(REGISTERED_NAME);
        String validVotes = electionData.get(VALID_VOTES);
        System.out.println(validVotes);

        election.getParties().stream().filter(party -> Objects.equals(party.getId(), affiliationId))
                .findFirst()
                .ifPresent(party -> party.setVotes(Integer.parseInt(validVotes)));

        System.out.printf("%s party votes: %s\n", aggregated ? "National" : "Constituency", electionData);
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {
        String affiliationId = electionData.get(AFFILIATION_IDENTIFIER_ID);
        String candidateShortCode = electionData.get(SHORT_CODE);
        String validVotes = electionData.get(VALID_VOTES);

        election.getParties().stream().filter(party -> Objects.equals(party.getId(), affiliationId))
                .findFirst().flatMap(party -> party.getCandidates().stream()
                        .filter(candidate -> Objects.equals(candidate.getShortCode(), candidateShortCode))
                        .findFirst()).ifPresent(candidate -> candidate.setVotes(Integer.parseInt(validVotes)));
        System.out.printf("%s candidate votes: %s\n", aggregated ? "National" : "Constituency", electionData);
    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        System.out.printf("%s meta data: %s\n", aggregated ? "National" : "Constituency", electionData);
    }
}
