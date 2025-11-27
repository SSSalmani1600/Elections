package nl.hva.election_backend.entity;

import jakarta.persistence.*;
import java.util.List;

// Database entiteit: representeert een gebruiker in de database
// Deze class wordt door JPA gebruikt om gebruikersdata op te slaan en op te halen
@Entity
@Table(name = "users")
public class UserEntity {

    // Primaire sleutel: automatisch gegenereerd door de database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Gebruikersnaam (verplicht veld)
    @Column(nullable = false)
    private String username;

    // Wachtwoord hash: wachtwoord wordt nooit als plain text opgeslagen, alleen de hash
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // E-mailadres (verplicht veld)
    @Column(nullable = false)
    private String email;

    // Of deze gebruiker admin rechten heeft
    @Column(name = "is_admin")
    private Boolean isAdmin;

    // Lege constructor: vereist door Hibernate/JPA
    public UserEntity() {}

    // Relatie naar discussies: 1 gebruiker kan meerdere discussies hebben
    // LAZY betekent dat discussies pas worden opgehaald als je ze nodig hebt
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<DiscussionEntity> discussions;

    // Relatie naar reacties: 1 gebruiker kan meerdere reacties hebben
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<ReactionEntity> reactions;

    // ------------ GETTERS & SETTERS --------------

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getIsAdmin() { return isAdmin; }
    public void setIsAdmin(Boolean admin) { this.isAdmin = admin; }
}
