package nl.hva.election_backend.service;

import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repo.UserRepository;
import nl.hva.election_backend.security.BCryptPasswordHasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AuthService {
    private final UserRepository userRepo;
    private final BCryptPasswordHasher hasher = new BCryptPasswordHasher();

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Transactional(readOnly = true)
    public User authenticate(String email, String password) {
        return userRepo.findByEmail(normalizeEmail(email))
                .filter(u -> hasher.matches(password, u.getPasswordHash()))
                .orElse(null);
    }


    public User register(String email, String rawPassword, String username) {
        if (email == null || email.isBlank()
                || rawPassword == null || rawPassword.isBlank()
                || username == null || username.isBlank()) {
            throw new IllegalArgumentException("Email, password en username zijn verplicht");
        }

        String normalizedEmail = normalizeEmail(email);

        // simpele checks (pas aan naar jouw wensen)
        if (!normalizedEmail.contains("@")) {
            throw new IllegalArgumentException("Ongeldig e-mailadres");
        }
        if (rawPassword.length() < 8) {
            throw new IllegalArgumentException("Wachtwoord moet minimaal 8 karakters zijn");
        }

        if (userRepo.existsByEmail(normalizedEmail)) {
            throw new IllegalStateException("E-mail is al in gebruik");
        }

        // Hash via DI hasher; fallback naar expliciete BCrypt als hasher om wat voor reden dan ook null is.
        String passwordHash = hasher.hash(rawPassword);

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setUsername(username);
        user.setPasswordHash(passwordHash);

        return userRepo.save(user);
    }

    // Handige helper om e-mails consistent op te slaan/te zoeken
    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}

