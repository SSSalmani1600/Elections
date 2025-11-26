package nl.hva.election_backend.dto;

public class TokenRefreshResponse {
    private String refreshTokenHash;
    private String accessToken;

    public TokenRefreshResponse(String refreshTokenHash, String accessToken) {
        this.refreshTokenHash = refreshTokenHash;
        this.accessToken = accessToken;
    }

    public String getRefreshTokenHash() {
        return refreshTokenHash;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
