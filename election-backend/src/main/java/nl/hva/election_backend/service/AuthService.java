package nl.hva.election_backend.service;

import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repo.InMemoryUserRepository;
import nl.hva.election_backend.security.BCryptPasswordHasher;
import nl.hva.election_backend.security.PasswordHasher;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final InMemoryUserRepository userRepo;
    private final PasswordHasher hasher;
    private final BCryptPasswordHasher bCryptPasswordHasher = new BCryptPasswordHasher();

    public AuthService(InMemoryUserRepository userRepo, PasswordHasher hasher) {
        this.userRepo = userRepo;
        this.hasher = hasher;
    }

    public User authenticate(String email, String password) {
        return userRepo.findByEmail(email.trim().toLowerCase())
                .filter(u -> hasher.matches(password, u.getPasswordHash()))
                .orElse(null);
    }
}
