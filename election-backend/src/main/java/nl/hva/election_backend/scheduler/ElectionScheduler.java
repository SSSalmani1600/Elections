package nl.hva.election_backend.scheduler;

import nl.hva.election_backend.service.ElectionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ElectionScheduler {

    private final ElectionService electionService;

    public ElectionScheduler(ElectionService electionService) {
        this.electionService = electionService;
    }

    // cron
    @Scheduled(cron = "0 0 2 * * *")
    public void updateElections() {
        try {
            System.out.println("🔄 Updating election data...");
            var data = electionService.fetchUpcomingElections();
            System.out.println("✅ " + data.size() + " elections found and updated.");
        } catch (Exception e) {
            System.err.println("❌ Error while updating elections: " + e.getMessage());
        }
    }
}
