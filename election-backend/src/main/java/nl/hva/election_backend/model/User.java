package nl.hva.election_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;

// Model class: representeert een gebruiker in de applicatie
// Dit is zowel een database entiteit als een model class
@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    // Primaire sleutel: automatisch gegenereerd door de database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Gebruikersnaam (uniek en verplicht)
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    // E-mailadres (uniek en verplicht)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // Wachtwoord hash: nooit plain text opslaan
    @Column(name = "password_hash")
    private String passwordHash;

    // Of deze gebruiker admin rechten heeft
    @Column(name = "is_admin")
    private boolean isAdmin;

    // Relatie naar discussies: 1 gebruiker kan meerdere discussies hebben
    // LAZY betekent dat discussies pas worden opgehaald als je ze nodig hebt
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<nl.hva.election_backend.entity.DiscussionEntity> discussions;

    // Relatie naar reacties: 1 gebruiker kan meerdere reacties hebben
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<nl.hva.election_backend.entity.ReactionEntity> reactions;

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    // Lege constructor: vereist door JPA
    public User() {
    }

    // Constructor voor nieuwe gebruikers: standaard geen admin rechten
    public User(String email, String passwordHash, String username) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.username = username;
        this.isAdmin = false; // Nieuwe gebruikers zijn standaard geen admin
    }

    // ⭐ GETTERS & SETTERS

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }
}