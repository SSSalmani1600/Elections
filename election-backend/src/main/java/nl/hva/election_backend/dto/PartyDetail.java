package nl.hva.election_backend.dto;

import java.util.List;

public class PartyDetail {
    public String partyId;
    public String name;
    public int year;
    public List<Candidate> candidates;
}
