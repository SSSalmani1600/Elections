package nl.hva.election_backend.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String email;

    @Column(name = "is_admin")
    private Boolean isAdmin;

    // ⭐ BELANGRIJK! Hibernate had deze nodig
    public UserEntity() {}

    // -------------- RELATIE FIX -------------------
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<DiscussionEntity> discussions;

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
