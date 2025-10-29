package nl.hva.election_backend.utils.xml.transformers;

import nl.hva.election_backend.model.Candidate;
import nl.hva.election_backend.model.Election;
import nl.hva.election_backend.model.Party;
import nl.hva.election_backend.utils.xml.TagAndAttributeNames;
import nl.hva.election_backend.utils.xml.VotesTransformer;

import java.util.Map;

public class DutchMunicipalityVotesTransformer implements VotesTransformer {
    private final Election election;

    public DutchMunicipalityVotesTransformer(Election election) {
        this.election = election;
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> data) {
        String partyName = data.get(TagAndAttributeNames.AFFILIATION_IDENTIFIER_ID);
        String votesStr = data.get(TagAndAttributeNames.VALID_VOTES);
        if (partyName == null || votesStr == null) return;

        int votes = safeInt(votesStr);

        String municipalityId = data.get(TagAndAttributeNames.SUPERIOR_REGION_NUMBER);
        String municipalityName = data.get(TagAndAttributeNames.REGION_NAME);
        String stationId = data.get(TagAndAttributeNames.REPORTING_UNIT_IDENTIFIER);

        // Gebruik bestaande methode uit Election
        Party party = election.getPartyByName(partyName);
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
        String candidateName = data.get(TagAndAttributeNames.CANDIDATE_FULL_NAME);
        String votesStr = data.get(TagAndAttributeNames.VALID_VOTES);
        if (candidateName == null || votesStr == null) return;

        int votes = safeInt(votesStr);

        String municipalityId = data.get(TagAndAttributeNames.SUPERIOR_REGION_NUMBER);
        String municipalityName = data.get(TagAndAttributeNames.REGION_NAME);
        String stationId = data.get(TagAndAttributeNames.REPORTING_UNIT_IDENTIFIER);

        // Gebruik bestaande methode uit Election
        Candidate candidate = election.getCandidateByName(candidateName);
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
    public void registerMetadata(boolean aggregated, Map<String, String> data) {
        // Niet nodig voor deze transformer
    }

    // ---------- helpers ----------
    private static int safeInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }

    private static String safeKey(String id, String fallbackName) {
        if (id != null && !id.isBlank()) return id.trim();
        if (fallbackName != null && !fallbackName.isBlank()) return fallbackName.trim();
        return "UNKNOWN";
    }
}
