package nl.hva.election_backend.dto;

import nl.hva.election_backend.model.User;

public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    private User user;

    public AuthenticationResponse(String accessToken, String refreshToken, User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public User getUser() {
        return user;
    }
}
