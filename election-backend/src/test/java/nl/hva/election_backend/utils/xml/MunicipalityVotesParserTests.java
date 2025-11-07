package nl.hva.election_backend.utils.xml.transformers;

import nl.hva.election_backend.model.Candidate;
import nl.hva.election_backend.model.Election;
import nl.hva.election_backend.model.Party;
import nl.hva.election_backend.utils.xml.TagAndAttributeNames;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test voor DutchMunicipalityVotesTransformer
 * Test of stemmen goed worden geregistreerd per gemeente en per stembureau.
 */
class DutchMunicipalityVotesTransformerTest {

    private Election election;
    private Party vvd;
    private Party d66;
    private Candidate mark;
    private Candidate sigrid;
    private DutchMunicipalityVotesTransformer transformer;

    @BeforeEach
    void setup() {
        // Maak dummy election, partijen en kandidaten
        election = new Election("TK2023", "Tweede Kamerverkiezingen");
        vvd = new Party("VVD");
        d66 = new Party("D66");
        election.getParties().add(vvd);
        election.getParties().add(d66);

        mark = new Candidate("Mark Rutte", vvd);
        sigrid = new Candidate("Sigrid Kaag", d66);
        election.getCandidates().add(mark);
        election.getCandidates().add(sigrid);

        transformer = new DutchMunicipalityVotesTransformer(election);
    }

    @Test
    void testRegisterPartyVotesAggregated() {
        Map<String, String> data = new HashMap<>();
        data.put(TagAndAttributeNames.AFFILIATION_IDENTIFIER_ID, "VVD");
        data.put(TagAndAttributeNames.VALID_VOTES, "1200");
        data.put(TagAndAttributeNames.SUPERIOR_REGION_NUMBER, "0453");
        data.put(TagAndAttributeNames.REGION_NAME, "Velsen");

        transformer.registerPartyVotes(true, data);

        assertEquals(1200, vvd.getVotesByMunicipality().get("0453"));
        assertTrue(vvd.getVotesByStation().isEmpty());
    }

    @Test
    void testRegisterPartyVotesStationLevel() {
        Map<String, String> data = new HashMap<>();
        data.put(TagAndAttributeNames.AFFILIATION_IDENTIFIER_ID, "D66");
        data.put(TagAndAttributeNames.VALID_VOTES, "300");
        data.put(TagAndAttributeNames.REPORTING_UNIT_IDENTIFIER, "0453::SB1");
        data.put(TagAndAttributeNames.REGION_NAME, "Velsen");

        transformer.registerPartyVotes(false, data);

        assertEquals(300, d66.getVotesByStation().get("0453::SB1"));
        assertTrue(d66.getVotesByMunicipality().isEmpty());
    }

    @Test
    void testRegisterCandidateVotesAggregated() {
        Map<String, String> data = new HashMap<>();
        data.put(TagAndAttributeNames.CANDIDATE_FULL_NAME, "Mark Rutte");
        data.put(TagAndAttributeNames.VALID_VOTES, "800");
        data.put(TagAndAttributeNames.SUPERIOR_REGION_NUMBER, "0453");
        data.put(TagAndAttributeNames.REGION_NAME, "Velsen");

        transformer.registerCandidateVotes(true, data);

        assertEquals(800, mark.getVotesByMunicipality().get("0453"));
        assertTrue(mark.getVotesByStation().isEmpty());
    }

    @Test
    void testRegisterCandidateVotesStationLevel() {
        Map<String, String> data = new HashMap<>();
        data.put(TagAndAttributeNames.CANDIDATE_FULL_NAME, "Sigrid Kaag");
        data.put(TagAndAttributeNames.VALID_VOTES, "500");
        data.put(TagAndAttributeNames.REPORTING_UNIT_IDENTIFIER, "0453::SB2");
        data.put(TagAndAttributeNames.REGION_NAME, "Velsen");

        transformer.registerCandidateVotes(false, data);

        assertEquals(500, sigrid.getVotesByStation().get("0453::SB2"));
        assertTrue(sigrid.getVotesByMunicipality().isEmpty());
    }
}
