package nl.hva.election_backend.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_rt_user_id", columnList = "user_id"),
                @Index(name = "idx_rt_family_id", columnList = "family_id"),
                @Index(name = "idx_rt_expires_at", columnList = "expires_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_rt_token_hash", columnNames = "token_hash")
        }
)
public class RefreshToken {

    @Id
    @Column(name = "id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    // Match your users.id type: BIGINT in your DB error
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // Optional: link to a device table if you have one
    @Column(name = "device_id")
    private Long deviceId;

    @Column(name = "family_id", nullable = false, columnDefinition = "uuid")
    private UUID familyId;

    // Store a SHA-256 (hex/base64) of the refresh token, not the raw token
    @Column(name = "token_hash", nullable = false, length = 64)
    private String tokenHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "replaced_by", columnDefinition = "uuid")
    private UUID replacedBy;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    // Keep JSON as TEXT for portability; you can switch to JSONB with Hibernate 6 if you want
    @Lob
    @Column(name = "meta")
    private String meta;

    /* getters/setters/constructors… */
}
