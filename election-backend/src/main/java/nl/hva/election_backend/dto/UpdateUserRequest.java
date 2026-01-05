package nl.hva.election_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// DTO voor het updaten van gebruikersgegevens
// Dit object wordt gebruikt wanneer een gebruiker zijn accountgegevens wil wijzigen
public class UpdateUserRequest {
    @NotBlank(message = "Huidig wachtwoord is verplicht")
    private String currentPassword; // Verplicht: huidig wachtwoord voor verificatie

    @Size(min = 3, max = 50, message = "Gebruikersnaam moet tussen de 3 en 50 karakters zijn")
    private String username;         // Optioneel: nieuwe gebruikersnaam

    @Email(message = "Ongeldig e-mailadres")
    private String email;            // Optioneel: nieuw email adres

    @Size(min = 8, message = "Nieuw wachtwoord moet minimaal 8 karakters bevatten")
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

