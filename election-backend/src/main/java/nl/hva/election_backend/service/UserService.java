package nl.hva.election_backend.service;

// Service voor gebruikersbeheer (account pagina)

import nl.hva.election_backend.dto.UpdateUserRequest;
import nl.hva.election_backend.entity.DiscussionEntity;
import nl.hva.election_backend.entity.ReactionEntity;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repository.DiscussionRepository;
import nl.hva.election_backend.repository.ReactionRepository;
import nl.hva.election_backend.repository.TestRepository;
import nl.hva.election_backend.security.BCryptPasswordHasher;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final TestRepository userRepository;
    private final DiscussionRepository discussionRepository;
    private final ReactionRepository reactionRepository;
    private final BCryptPasswordHasher passwordHasher;

    // constructor injection
    public UserService(TestRepository userRepository,
                       DiscussionRepository discussionRepository,
                       ReactionRepository reactionRepository,
                       BCryptPasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.discussionRepository = discussionRepository;
        this.reactionRepository = reactionRepository;
        this.passwordHasher = passwordHasher;
    }

    // haal user op via id
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // check of user bestaat
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    // update user gegevens
    public User updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gebruiker niet gevonden"));

        // check huidig wachtwoord
        if (request.getCurrentPassword() == null || request.getCurrentPassword().isBlank()) {
            throw new IllegalArgumentException("Huidig wachtwoord is verplicht");
        }
        if (!passwordHasher.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new SecurityException("Huidig wachtwoord is onjuist");
        }

        // update username
        if (request.getUsername() != null && !request.getUsername().isBlank()) {
            user.setUsername(request.getUsername().trim());
        }

        // update email
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String normalizedEmail = request.getEmail().trim().toLowerCase();
            Optional<User> existing = userRepository.findByEmail(normalizedEmail);
            if (existing.isPresent() && !existing.get().getId().equals(id)) {
                throw new IllegalStateException("E-mail is al in gebruik");
            }
            user.setEmail(normalizedEmail);
        }

        // update wachtwoord
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (request.getPassword().length() < 8) {
                throw new IllegalArgumentException("Nieuw wachtwoord moet minimaal 8 karakters zijn");
            }
            user.setPasswordHash(passwordHasher.hash(request.getPassword()));
        }

        return userRepository.save(user);
    }

    // haal activiteit op (topics + reacties)
    public Map<String, Object> getUserActivity(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Gebruiker niet gevonden");
        }

        // topics
        List<DiscussionEntity> discussions = discussionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<Map<String, Object>> topicsList = new ArrayList<>();
        for (DiscussionEntity d : discussions) {
            Map<String, Object> topic = new HashMap<>();
            topic.put("id", d.getId());
            topic.put("title", d.getTitle());
            topic.put("createdAt", d.getCreatedAt().toString());
            topic.put("reactionsCount", d.getReactionsCount());
            topicsList.add(topic);
        }

        // reacties
        List<ReactionEntity> reactions = reactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
        List<Map<String, Object>> reactionsList = new ArrayList<>();
        for (ReactionEntity r : reactions) {
            Map<String, Object> reaction = new HashMap<>();
            reaction.put("id", r.getId());
            reaction.put("message", r.getMessage());
            reaction.put("createdAt", r.getCreatedAt().toString());
            reaction.put("discussionId", r.getDiscussion().getId());
            reaction.put("discussionTitle", r.getDiscussion().getTitle());
            reactionsList.add(reaction);
        }

        Map<String, Object> activity = new HashMap<>();
        activity.put("topics", topicsList);
        activity.put("reactions", reactionsList);
        return activity;
    }
}

