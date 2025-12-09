package nl.hva.election_backend.dto;

import java.util.List;

public class ModerationResponse {
    private final boolean isToxic;
    private final List<String> flaggedWords;
    private final String toxicityReason;
    private final boolean isMisinformation;
    private final String misinformationDetails;


    public ModerationResponse(boolean isToxic, List<String> flaggedWords, String toxicityReason,
                              boolean isMisinformation, String misinformationDetails) {
        this.isToxic = isToxic;
        this.flaggedWords = flaggedWords;
        this.toxicityReason = toxicityReason;
        this.isMisinformation = isMisinformation;
        this.misinformationDetails = misinformationDetails;
    }

    public ModerationResponse() {
        this(false, null, null, false, null);
    }



    public boolean isToxic() {
        return isToxic;
    }

    public List<String> getFlaggedWords() {
        return flaggedWords;
    }

    public String getToxicityReason() {
        return toxicityReason;
    }

    public boolean isMisinformation() {
        return isMisinformation;
    }

    public String getMisinformationDetails() {
        return misinformationDetails;
    }
}