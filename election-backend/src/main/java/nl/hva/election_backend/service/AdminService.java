package nl.hva.election_backend.service;

import nl.hva.election_backend.repository.ReactionRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final JdbcTemplate jdbcTemplate;
    private final ReactionRepository reactionRepository;

    public AdminService(JdbcTemplate jdbcTemplate, ReactionRepository reactionRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.reactionRepository = reactionRepository;
    }


    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) FROM public.users";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }


    public int getReportedPosts() {
        String sql = "SELECT COUNT(*) FROM public.discussions";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    // ✔️ Openstaande reviews → niet aanwezig → return 0
    public int getPendingReviews() {
        return reactionRepository.findByModerationStatus("PENDING").size();
    }
}
