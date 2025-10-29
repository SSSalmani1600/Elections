package nl.hva.election_backend.utils.xml.transformers;


import nl.hva.election_backend.model.Candidate;
import nl.hva.election_backend.model.Election;
import nl.hva.election_backend.model.Party;
import nl.hva.election_backend.utils.xml.TagAndAttributeNames;
import nl.hva.election_backend.utils.xml.VotesTransformer;

import java.util.Map;

/**
 * Just prints to content of electionData to the standard output.>br/>
 * <b>This class needs heavy modification!</b>
 */
public class DutchMunicipalityVotesTransformer implements VotesTransformer {
    private final Election election;

    /**
     * Creates a new transformer for handling the votes at the municipality level. It expects an instance of
     * Election that can be used for storing the results.
     * @param election the election in which the votes wil be stored.
     */
    public DutchMunicipalityVotesTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> data) {
        String partyId = data.get(TagAndAttributeNames.AFFILIATION_IDENTIFIER_ID);
        String votesStr = data.get(TagAndAttributeNames.VALID_VOTES);
        if (partyId == null || votesStr == null) return;

        int votes = safeInt(votesStr);

        String municipalityId = data.get(TagAndAttributeNames.SUPERIOR_REGION_NUMBER);
        String municipalityName = data.get(TagAndAttributeNames.REGION_NAME);
        String stationId = data.get(TagAndAttributeNames.REPORTING_UNIT_IDENTIFIER_ID);

        Party party = election.findPartyById(partyId);
        if (party == null) return;

        if (aggregated) {
            String key = safeKey(municipalityId, municipalityName);
            party.getVotesByMunicipality().merge(key, votes, Integer::sum);
        } else {
            String key = safeKey(stationId, municipalityName);
            party.getVotesByStation().merge(key, votes, Integer::sum);
        }
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> data) {
        String partyId = data.get(TagAndAttributeNames.AFFILIATION_IDENTIFIER_ID);
        String candidateKey = firstNonNull(
                data.get(TagAndAttributeNames.CANDIDATE_IDENTIFIER_SHORT_CODE),
                data.get(TagAndAttributeNames.CANDIDATE_IDENTIFIER_ID)
        );
        String votesStr = data.get(TagAndAttributeNames.VALID_VOTES);
        if (partyId == null || candidateKey == null || votesStr == null) return;

        int votes = safeInt(votesStr);

        String municipalityId = data.get(TagAndAttributeNames.SUPERIOR_REGION_NUMBER);
        String municipalityName = data.get(TagAndAttributeNames.REGION_NAME);
        String stationId = data.get(TagAndAttributeNames.REPORTING_UNIT_IDENTIFIER_ID);

        Party party = election.findPartyById(partyId);
        if (party == null) return;

        Candidate candidate = election.findCandidateByPartyAndKey(party, candidateKey);
        if (candidate == null) return;

        if (aggregated) {
            String key = safeKey(municipalityId, municipalityName);
            candidate.getVotesByMunicipality().merge(key, votes, Integer::sum);
        } else {
            String key = safeKey(stationId, municipalityName);
            candidate.getVotesByStation().merge(key, votes, Integer::sum);
        }
    }

    @Override
    public void registerMetadata(boolean aggregated, Map<String, String> electionData) {
        System.out.printf("%s meta data: %s\n", aggregated ? "Municipality" : "Polling station", electionData);
    }

}
