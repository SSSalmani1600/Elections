package nl.hva.election_backend.repository;

import nl.hva.election_backend.model.Poll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PollRepository extends JpaRepository<Poll, UUID> {

    Page<Poll> findAllByOrderByCreatedAtDesc(Pageable pageable);

}
