package nl.hva.election_backend.dto;

public class LoginResponse {
    private Long id;
    private String displayName;
    private String token;

    public LoginResponse() {}

    public LoginResponse(Long id, String token, String displayName) {
        this.id = id;
        this.token = token;
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LoginResponse(Long id, String token, String displayName) {
        this.id = id;
        this.displayName = displayName;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
