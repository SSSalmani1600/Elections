package nl.hva.election_backend.discussion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscussionConfig {

    @Bean
    public InMemoryDiscussionRepository discussionRepository() {
        // registreert je in-memory repo als Spring bean
        return new InMemoryDiscussionRepository();
    }
}
