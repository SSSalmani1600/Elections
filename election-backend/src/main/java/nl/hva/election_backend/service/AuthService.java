package nl.hva.election_backend.service;

import nl.hva.election_backend.dto.AuthenticationResponse;
import nl.hva.election_backend.model.RefreshToken;
import nl.hva.election_backend.model.User;
import nl.hva.election_backend.repo.RefreshTokenRepository;
import nl.hva.election_backend.repo.UserRepository;
import nl.hva.election_backend.security.BCryptPasswordHasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Transactional
@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserRepository userRepo;
    private final BCryptPasswordHasher hasher;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshRepo;
    private final String SECRET_KEY = System.getenv().getOrDefault("JWT_SECRET_B64",
            "dLRPokUNE7CfDTv2Nq1JmKZLuDSbMLvfTn9yJAxCx4A=");

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
            refreshTokenHash = generateRefreshToken();
            accessToken = jwtService.generateToken(user.getId().toString());
        } catch (Exception e) {
            log.error("e: ", e);
        }

        RefreshToken refreshToken = new RefreshToken(user.getId(), refreshTokenHash, Instant.now().plusSeconds(15 * 60 * 1000));

        refreshRepo.saveAndFlush(refreshToken);

        return new AuthenticationResponse(accessToken, refreshTokenHash, user);
    }

    public String generateRefreshToken() throws Exception {
        SecureRandom rnd = new SecureRandom();

        byte[] bytes = new byte[32];
        rnd.nextBytes(bytes);
        String b64 = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(b64.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(digest);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
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
