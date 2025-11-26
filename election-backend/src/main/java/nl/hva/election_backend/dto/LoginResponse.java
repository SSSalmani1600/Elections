package nl.hva.election_backend.dto;

public class LoginResponse {

    // ⭐ Jouw velden (nodig voor frontend admin + token opslag)
    private Long id;
    private String displayName;
    private String token;
    private Boolean isAdmin;

    // ⭐ Main veld (voor backwards compatibility en andere endpoints)
    private String username;

    public LoginResponse() {}

    // ⭐ Jouw constructor
    public LoginResponse(Long id, String token, String displayName, Boolean isAdmin) {
        this.id = id;
        this.token = token;
        this.displayName = displayName;
        this.username = displayName; // ← compatibel met main
        this.isAdmin = isAdmin;
    }

    // ⭐ Main constructor
    public LoginResponse(String username) {
        this.username = username;
        this.displayName = username; // ← voorkomt null issues
    }

    // ---- GETTERS & SETTERS ----

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getDisplayName() { return displayName; }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.username = displayName; // sync voor compatibiliteit
    }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

    public Boolean getIsAdmin() { return isAdmin; }

    public void setIsAdmin(Boolean isAdmin) { this.isAdmin = isAdmin; }

    public String getUsername() { return username; }

    public void setUsername(String username) {
        this.username = username;
        this.displayName = username; // sync
    }
}
