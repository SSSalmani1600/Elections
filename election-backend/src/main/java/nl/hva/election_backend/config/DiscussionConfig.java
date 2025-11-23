package nl.hva.election_backend.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration for discussions.
 * JPA repositories are automatically discovered by Spring, so no manual bean configuration is needed.
 */
@Configuration
public class DiscussionConfig {
    // In-memory repository has been replaced with JPA repositories (DiscussionRepository, ReactionRepository)
    // These are automatically discovered and registered by Spring Data JPA
}
