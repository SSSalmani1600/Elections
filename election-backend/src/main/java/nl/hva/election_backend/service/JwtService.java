package nl.hva.election_backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import nl.hva.election_backend.exception.InvalidRefreshTokenException;
import nl.hva.election_backend.model.RefreshToken;
import nl.hva.election_backend.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.*;
import java.util.*;

@Service
public class JwtService {
    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private final String SECRET_KEY = System.getenv().getOrDefault("JWT_SECRET_B64",
            "dLRPokUNE7CfDTv2Nq1JmKZLuDSbMLvfTn9yJAxCx4A=");
    private final String issuer = "ga-stemmen.nl";
    private final RefreshTokenRepository refreshRepo;

    public JwtService(RefreshTokenRepository refreshRepo) {
        this.refreshRepo = refreshRepo;
    }

    public String generateToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder().claims().add(claims).subject(String.valueOf(userId))
                .issuer(issuer).issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 uur
                .and().signWith(this.getKey()).compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean isTokenExpired(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration().before(new Date());
    }

    public Claims extractAllClaims(String token) {
        return Jwts
            .parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public String extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
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

    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }

        try {
            Claims claims = extractAllClaims(token);

            if (!issuer.equals(claims.getIssuer())) return false;
            return !isTokenExpired(token);

        } catch (Exception e) {
            return false;
        }
    }

    private boolean isRefreshTokenValid(String tokenHash) {
        Instant now = Instant.now();
        return refreshRepo.existsByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(tokenHash, now);
    }

    public RefreshToken rotateRefreshToken(String refreshToken) {
        Optional<RefreshToken> oldRefreshToken = refreshRepo.findByTokenHash(refreshToken);
        if (oldRefreshToken.isEmpty()) {
            throw new InvalidRefreshTokenException("Empty refresh token");
        };

        if (!isRefreshTokenValid(refreshToken)) {
            throw new InvalidRefreshTokenException("Invalid or expired refresh token");
        }

        try {
            String newTokenHash = this.generateRefreshToken();
            Instant newExpiryDate = Instant.now().plus(Duration.ofMinutes(15));
            RefreshToken newRefreshToken =
                    new RefreshToken(oldRefreshToken.get().getUserId(), newTokenHash, newExpiryDate);
            oldRefreshToken.get().setRevokedAt(Instant.now());
            newRefreshToken.setFamilyId(oldRefreshToken.get().getFamilyId());

            refreshRepo.saveAllAndFlush(java.util.List.of(oldRefreshToken.get(), newRefreshToken));
            return newRefreshToken;
        } catch(Exception e) {
            log.error("e: ", e);
            throw new RuntimeException("Server error while rotating refresh token");
        }
    }
}