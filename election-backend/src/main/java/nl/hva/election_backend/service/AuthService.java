package nl.hva.election_backend.service;

import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repo.InMemoryUserRepository;
import nl.hva.election_backend.security.PasswordHasher;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final InMemoryUserRepository userRepo;
    private final PasswordHasher hasher;

    public AuthService(InMemoryUserRepository userRepo, PasswordHasher hasher) {
        this.userRepo = userRepo;
        this.hasher = hasher;
    }

    public boolean authenticate(String email, String password) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return hasher.matches(password, user.getPasswordHash());
    }
}
