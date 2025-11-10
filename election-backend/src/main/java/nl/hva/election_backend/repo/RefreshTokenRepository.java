package nl.hva.election_backend.repo;

import nl.hva.election_backend.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    void save(Long id, String refreshToken, Instant time);
}
