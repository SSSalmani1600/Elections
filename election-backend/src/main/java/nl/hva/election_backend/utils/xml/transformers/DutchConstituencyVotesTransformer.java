package nl.hva.election_backend.utils.xml.transformers;


import nl.hva.election_backend.model.Constituency;
import nl.hva.election_backend.model.Election;
import nl.hva.election_backend.model.Party;
import nl.hva.election_backend.utils.xml.TagAndAttributeNames;
import nl.hva.election_backend.utils.xml.VotesTransformer;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Just prints to content of electionData to the standard output.>br/>
 * <b>This class needs heavy modification!</b>
 */
public class DutchConstituencyVotesTransformer implements VotesTransformer, TagAndAttributeNames {
    private final Election election;

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
        String affiliationId = electionData.get(AFFILIATION_IDENTIFIER_ID);
        String partyName = electionData.get(REGISTERED_NAME);
        election.getParties().stream().filter(party -> Objects.equals(party.getName(), partyName))
                .findFirst()
                .ifPresent(party -> party.setId(affiliationId));

        String constituencyName = electionData.get(CONTEST_NAME);
        election.getConstituencies().add(new Constituency(constituencyName));
        election.getConstituencies().forEach(constituency -> constituency.getParties().add(new Party(partyName)));
        election.getConstituencies().forEach(constituency -> constituency.getParties()
                .stream().filter(party -> Objects.equals(party.getName(), partyName))
                .findFirst()
                .ifPresent(party -> party.setId(affiliationId)));

        int votes = Integer.parseInt(electionData.get(VALID_VOTES));
        election.getConstituencies().forEach(constituency -> constituency.getParties()
                .stream().filter(party -> Objects.equals(party.getId(), affiliationId))
                .findFirst()
                .ifPresent(party -> party.setVotes(party.getVotes() + votes)));
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> electionData) {

    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        System.out.printf("%s meta data: %s\n", aggregated ? "Constituency" : "Municipality", electionData);
    }
}
