package nl.hva.election_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import nl.hva.election_backend.repo.InMemoryDiscussionRepository;


@Configuration
public class DiscussionConfig {

    @Bean
    public InMemoryDiscussionRepository discussionRepository() {
        // registreert je in-memory repo als Spring bean
        return new InMemoryDiscussionRepository();
    }
}
