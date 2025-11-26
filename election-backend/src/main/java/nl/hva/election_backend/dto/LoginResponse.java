package nl.hva.election_backend.dto;

import nl.hva.election_backend.model.User;

public class LoginResponse {
    private Long id;
    private String email;
    private String username;
    private boolean isAdmin;

    public LoginResponse() {
    }

    public LoginResponse(Long id, String email, String username, boolean isAdmin) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.isAdmin = isAdmin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
