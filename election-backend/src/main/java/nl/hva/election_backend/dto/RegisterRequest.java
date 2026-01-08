package nl.hva.election_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank(message = "Email is verplicht")
    @Email(message = "Ongeldig e-mailadres")
    private String email;

    @NotBlank(message = "Wachtwoord is verplicht")
    @Size(min = 8, message = "Wachtwoord moet minimaal 8 karakters zijn")
    private String password;

    @NotBlank(message = "Gebruikersnaam is verplicht")
    @Size(min = 3, max = 50, message = "Gebruikersnaam moet tussen 3 en 50 karakters zijn")
    private String username;

    public RegisterRequest() {}

    public RegisterRequest(String email, String password, String username) {
        this.email = email;
        this.password = password;
        this.username = username;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}



