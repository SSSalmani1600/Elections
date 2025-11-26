package nl.hva.election_backend.dto;

public class TokenValidationResponse {
    private boolean isValid;
    private boolean shouldRefresh;

    public TokenValidationResponse(boolean isValid, boolean shouldRefresh) {
        this.isValid = isValid;
        this.shouldRefresh = shouldRefresh;
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean shouldRefresh() {
        return shouldRefresh;
    }
}
