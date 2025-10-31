package nl.hva.election_backend.repo;

import nl.hva.election_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<User, Long> {
}
