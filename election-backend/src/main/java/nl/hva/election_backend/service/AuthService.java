package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.AuthenticationResponse;
import nl.hva.election_backend.dto.TokenRefreshResponse;
import nl.hva.election_backend.model.RefreshToken;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repository.RefreshTokenRepository;
import nl.hva.election_backend.repository.UserRepository;
import nl.hva.election_backend.security.BCryptPasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Transactional
@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepo;
    private final BCryptPasswordHasher hasher;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshRepo;

    public AuthService(UserRepository userRepo, BCryptPasswordHasher hasher, JwtService jwtService, RefreshTokenRepository refreshRepo) {
        this.userRepo = userRepo;
        this.hasher = hasher;
        this.jwtService = jwtService;
        this.refreshRepo = refreshRepo;
    }

    @Transactional
    public AuthenticationResponse authenticate(String email, String password) {
        User user = userRepo.findByEmail(normalizeEmail(email))
                .filter(u -> hasher.matches(password, u.getPasswordHash()))
                .orElse(null);

        if (user == null) return new AuthenticationResponse();

        String accessToken = "";
        String refreshTokenHash = "";

        try {
            refreshTokenHash = jwtService.generateRefreshToken();
            accessToken = jwtService.generateToken(user.getId());
        } catch (Exception e) {
            log.error("e: ", e);
        }

        RefreshToken refreshToken = new RefreshToken(user.getId(), refreshTokenHash, Instant.now().plusSeconds(15 * 60 * 1000));

        refreshRepo.saveAndFlush(refreshToken);

        return new AuthenticationResponse(accessToken, refreshTokenHash, user);
    }
    public TokenRefreshResponse refreshTokens(String refreshTokenHash) {
        RefreshToken newRefreshToken = jwtService.rotateRefreshToken(refreshTokenHash);

        String newAccessToken = jwtService.generateToken(newRefreshToken.getUserId());

        return new TokenRefreshResponse(newRefreshToken.getTokenHash(), newAccessToken);
    }

    public User register(String email, String rawPassword, String username) {
        if (email == null || email.isBlank()
                || rawPassword == null || rawPassword.isBlank()
                || username == null || username.isBlank()) {
            throw new IllegalArgumentException("Email, password en username zijn verplicht");
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
        user.setUsername(username.trim());
        user.setPasswordHash(passwordHash);


        return userRepo.save(user);
    }


    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
