package nl.hva.election_backend.controller;

import nl.hva.election_backend.security.AdminOnly;
import nl.hva.election_backend.service.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @AdminOnly
    @GetMapping("/stats")
    public Map<String, Object> getAdminStats() {

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", adminService.getTotalUsers());
        stats.put("reportedPosts", adminService.getReportedPosts());
        stats.put("pendingReviews", adminService.getPendingReviews());

        return stats;
    }
}


