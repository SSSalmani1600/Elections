package nl.hva.election_backend.dto;

// DTO voor het updaten van gebruikersgegevens
// Dit object wordt gebruikt wanneer een gebruiker zijn accountgegevens wil wijzigen
public class UpdateUserRequest {
    private String currentPassword; // Verplicht: huidig wachtwoord voor verificatie
    private String username;         // Optioneel: nieuwe gebruikersnaam
    private String email;            // Optioneel: nieuw email adres
    private String password;         // Optioneel: nieuw wachtwoord

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

