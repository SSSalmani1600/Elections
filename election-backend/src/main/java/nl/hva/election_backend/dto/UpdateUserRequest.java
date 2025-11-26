package nl.hva.election_backend.dto;

public class UpdateUserRequest {
    private String currentPassword; // Verplicht voor verificatie
    private String username;
    private String email;
    private String password; // Nieuw wachtwoord (optioneel)

    public UpdateUserRequest() {}

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

