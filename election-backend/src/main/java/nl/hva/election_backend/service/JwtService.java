package nl.hva.election_backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import nl.hva.election_backend.dto.TokenValidationResponse;
import nl.hva.election_backend.model.RefreshToken;
import nl.hva.election_backend.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    public String extractUsername(String token) {
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

    public TokenValidationResponse validateToken(String token) {
        Claims claims = extractAllClaims(token);
        if (!claims.getIssuer().equals("ga-stemmen.nl")) {
            return new TokenValidationResponse(false, false);
        }
        if (isTokenExpired(token)) {
            return new TokenValidationResponse(false, false);
        };

        Date expirationDate = claims.getExpiration();
        long remainingTime = expirationDate.getTime() - (new Date().getTime());
        long totalTime =  expirationDate.getTime() - claims.getIssuedAt().getTime();

        if ((remainingTime / totalTime) * 100 <= 25) {
            return new TokenValidationResponse(true, true);
        }

        return new TokenValidationResponse(true, false);
    }

    private boolean isRefreshTokenValid(String tokenHash) {
        Instant now = Instant.now();
        return refreshRepo.existsByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(tokenHash, now);
    }

    public RefreshToken refreshToken(String refreshToken) {
        Optional<RefreshToken> oldRefreshToken = refreshRepo.findByTokenHash(refreshToken);
        if (oldRefreshToken.isEmpty()) {
            return null;
        };

        if (!isRefreshTokenValid(refreshToken)) {
            return null;
        }

        try {
            String newTokenHash = this.generateRefreshToken();
            Instant newExpiryDate = Instant.now().plusSeconds(15 * 60 * 1000);
            RefreshToken newRefreshToken =
                    new RefreshToken(oldRefreshToken.get().getUserId(), newTokenHash, newExpiryDate);
            oldRefreshToken.get().setRevokedAt(Instant.now());
            newRefreshToken.setFamilyId(oldRefreshToken.get().getFamilyId());

            refreshRepo.saveAllAndFlush(java.util.List.of(oldRefreshToken.get(), newRefreshToken));
            return newRefreshToken;
        } catch(Exception e) {
            log.error("e: ", e);
        }

        return null;
    }
}
