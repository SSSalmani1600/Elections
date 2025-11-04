package nl.hva.election_backend.repo;

import nl.hva.election_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Find by username
    Optional<User> findByUsername(String username);

    // Find by email
    Optional<User> findByEmail(String email);

    // Find all active users
    List<User> findByActiveTrue();

    // Find all users with a given role
    List<User> findByRole(String role);

    // Check if a user exists by email
    boolean existsByEmail(String email);
}
