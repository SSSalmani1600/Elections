package nl.hva.election_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AdminController {

    @GetMapping("/admin/stats")
    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", 15);
        stats.put("reportedPosts", 4);
        stats.put("pendingReviews", 2);

        return stats;
    }
}
