package nl.hva.election_backend.utils.xml.transformers;

import nl.hva.election_backend.model.Candidate;
import nl.hva.election_backend.model.Election;
import nl.hva.election_backend.model.Party;
import nl.hva.election_backend.utils.xml.TagAndAttributeNames;
import nl.hva.election_backend.utils.xml.VotesTransformer;

import java.util.Map;

// leest stemmen per gemeente of stembureau en zet ze in election
public class DutchMunicipalityVotesTransformer implements VotesTransformer {
    private final Election election; // hier worden stemmen opgeslagen

    public DutchMunicipalityVotesTransformer(Election election) {
        this.election = election; // sla election op
    }

    @Override
    public void registerPartyVotes(boolean aggregated, Map<String, String> data) {
        // laat zien dat functie is gestart + wat er binnenkomt
        System.out.println("🟩 [DEBUG] registerPartyVotes() aangeroepen");
        System.out.println("  aggregated = " + aggregated);
        System.out.println("  data keys  = " + data.keySet());

        // haal naam van partij en stemmen eruit
        String partyName = data.get(TagAndAttributeNames.AFFILIATION_IDENTIFIER_ID);
        String votesStr = data.get(TagAndAttributeNames.VALID_VOTES);
        if (partyName == null || votesStr == null) return; // niks? dan skippen

        int votes = safeInt(votesStr); // zet tekst om naar getal

        // pak info over gemeente of stembureau
        String municipalityId = data.get(TagAndAttributeNames.SUPERIOR_REGION_NUMBER);
        String municipalityName = data.get(TagAndAttributeNames.REGION_NAME);
        String stationId = data.get(TagAndAttributeNames.REPORTING_UNIT_IDENTIFIER);

        // laat info zien in console
        System.out.printf("  🗳️ Party=%s | votes=%d | region=%s | station=%s%n",
                partyName, votes, municipalityName, stationId);

        // zoek partij in election
        Party party = election.getPartyByName(partyName);
        if (party == null) {
            System.out.println("  ⚠️ Geen partij gevonden voor: " + partyName);
            return;
        }

        // als het per gemeente is, opslaan bij gemeente
        if (aggregated) {
            String key = safeKey(municipalityId, municipalityName);
            party.getVotesByMunicipality().merge(key, votes, Integer::sum);
        } else {
            // anders opslaan bij stembureau
            String key = safeKey(stationId, municipalityName);
            party.getVotesByStation().merge(key, votes, Integer::sum);
        }
    }

    @Override
    public void registerCandidateVotes(boolean aggregated, Map<String, String> data) {
        // laat zien dat functie is gestart + wat er binnenkomt
        System.out.println("🟦 [DEBUG] registerCandidateVotes() aangeroepen");
        System.out.println("  aggregated = " + aggregated);
        System.out.println("  data keys  = " + data.keySet());

        // haal naam van kandidaat en stemmen eruit
        String candidateName = data.get(TagAndAttributeNames.CANDIDATE_FULL_NAME);
        String votesStr = data.get(TagAndAttributeNames.VALID_VOTES);
        if (candidateName == null || votesStr == null) return;

        int votes = safeInt(votesStr); // zet tekst om naar getal

        // pak info over gemeente of stembureau
        String municipalityId = data.get(TagAndAttributeNames.SUPERIOR_REGION_NUMBER);
        String municipalityName = data.get(TagAndAttributeNames.REGION_NAME);
        String stationId = data.get(TagAndAttributeNames.REPORTING_UNIT_IDENTIFIER);

        // laat info zien in console
        System.out.printf("  👤 Candidate=%s | votes=%d | region=%s | station=%s%n",
                candidateName, votes, municipalityName, stationId);

        // zoek kandidaat in election
        Candidate candidate = election.getCandidateByName(candidateName);
        if (candidate == null) {
            System.out.println("  ⚠️ Geen kandidaat gevonden voor: " + candidateName);
            return;
        }

        // stemmen opslaan bij gemeente of stembureau
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
        // hier doen we niks mee, niet nodig voor dit bestand
    }

    // helpers
    private static int safeInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; } // zet tekst naar getal, anders 0
    }

    private static String safeKey(String id, String fallbackName) {
        // kiest veilige naam, gebruikt id of naam, anders UNKNOWN
        if (id != null && !id.isBlank()) return id.trim();
        if (fallbackName != null && !fallbackName.isBlank()) return fallbackName.trim();
        return "UNKNOWN";
    }
}
