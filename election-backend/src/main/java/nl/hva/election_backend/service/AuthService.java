package nl.hva.election_backend.service;

import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repository.TestRepository;
import nl.hva.election_backend.security.BCryptPasswordHasher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final TestRepository userRepo;
    private final BCryptPasswordHasher hasher;

    public AuthService(TestRepository userRepo, BCryptPasswordHasher hasher) {
        this.userRepo = userRepo;
        this.hasher = hasher;
    }


    public User authenticate(String email, String password) {
        String normalizedEmail = normalizeEmail(email);
        Optional<User> optionalUser = userRepo.findByEmail(normalizedEmail);

        if (optionalUser.isEmpty()) {
            return null;
        }

        User user = optionalUser.get();
        if (hasher.matches(password, user.getPasswordHash())) {
            return user;
        }

        return null;
    }


    public User register(String email, String rawPassword, String displayName) {
        if (email == null || email.isBlank()
                || rawPassword == null || rawPassword.isBlank()
                || displayName == null || displayName.isBlank()) {
            throw new IllegalArgumentException("Email, password en displayName zijn verplicht");
        }

        String normalizedEmail = normalizeEmail(email);


        if (!normalizedEmail.contains("@")) {
            throw new IllegalArgumentException("Ongeldig e-mailadres");
        }
        if (rawPassword.length() < 8) {
            throw new IllegalArgumentException("Wachtwoord moet minimaal 8 karakters zijn");
        }

        if (userRepo.existsByEmail(normalizedEmail)) {
            throw new IllegalStateException("E-mail is al in gebruik");
        }


        String passwordHash = hasher.hash(rawPassword);


        User user = new User();
        user.setEmail(normalizedEmail);
        user.setUsername(displayName.trim());
        user.setPasswordHash(passwordHash);


        return userRepo.save(user);
    }


    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
