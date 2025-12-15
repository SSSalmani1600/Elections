package nl.hva.election_backend.repository;

import nl.hva.election_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Repository: interface voor database queries op gebruikers
// Spring genereert automatisch de implementatie
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Zoekt een gebruiker op basis van email adres
    // Gebruikt voor login en email validatie
    Optional<User> findByEmail(String email);

    // Checkt of een email adres al in gebruik is
    // Gebruikt bij registratie en email wijziging om duplicaten te voorkomen
    boolean existsByEmail(String email);

    // Checkt of een gebruikersnaam al in gebruik is
    // Gebruikt bij registratie om duplicaten te voorkomen
    boolean existsByUsername(String username);

    // Checkt of een gebruiker bestaat op basis van ID
    boolean existsById(Long id);
}
