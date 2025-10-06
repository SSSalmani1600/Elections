package nl.hva.election_backend.dto;

public class LoginResponse {
    private String displayName;
    private String token;

    public LoginResponse() {}

    public LoginResponse(String token, String displayName) {
        this.displayName = displayName;
        this.token = token;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
