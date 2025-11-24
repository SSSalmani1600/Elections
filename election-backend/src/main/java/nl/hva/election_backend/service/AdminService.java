package nl.hva.election_backend.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final JdbcTemplate jdbcTemplate;

    public AdminService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ✔️ Totaal aantal gebruikers
    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) FROM public.users";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    // ✔️ Gemelde berichten
    // Voor nu neem ik discussions (kan ook reactions zijn)
    public int getReportedPosts() {
        String sql = "SELECT COUNT(*) FROM public.discussions";
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    // ✔️ Openstaande reviews → niet aanwezig → return 0
    public int getPendingReviews() {
        return 0;
    }
}
