package nl.hva.election_backend.repository;

import nl.hva.election_backend.entity.CandidateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<CandidateEntity, String> {

    List<CandidateEntity> findByParty_PartyIdAndYearOrderByCandidateIdAsc(
            String partyId,
            int year
    );

}
