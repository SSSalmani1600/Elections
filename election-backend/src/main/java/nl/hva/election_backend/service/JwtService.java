package nl.hva.election_backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import nl.hva.election_backend.dto.TokenValidationResponse;
import nl.hva.election_backend.model.RefreshToken;
import nl.hva.election_backend.repo.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final String SECRET_KEY = System.getenv().getOrDefault(
            "JWT_SECRET_B64",
            "dLRPokUNE7CfDTv2Nq1JmKZLuDSbMLvfTn9yJAxCx4A="
    );

    private final String issuer = "ga-stemmen.nl";
    private final RefreshTokenRepository refreshRepo;

    public JwtService(RefreshTokenRepository refreshRepo) {
        this.refreshRepo = refreshRepo;
    }

    // ⭐ JOUW TOKEN: MET userId + displayName
    public String generateToken(Integer userId, String displayName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(displayName)
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .and()
                .signWith(this.getKey())
                .compact();
    }

    // ⭐ MAIN TOKEN (voor backwards compatibility)
    public String generateToken(String username) {
        return Jwts.builder()
                .claims()
                .subject(username)
                .issuer(issuer)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
                .and()
                .signWith(getKey())
                .compact();
    }

    // Shared key
    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration().before(new Date());
    }

    // ⭐ Jouw extractions
    public String extractDisplayName(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Integer extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Integer.class);
    }

    // ⭐ MAIN extraction
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // ⭐ Gecombineerde validatie met TokenValidationResponse
    public TokenValidationResponse validateToken(String token) {
        Claims claims;

        try {
            claims = extractAllClaims(token);
        } catch (Exception e) {
            return new TokenValidationResponse(false, false);
        }

        if (!issuer.equals(claims.getIssuer())) {
            return new TokenValidationResponse(false, false);
        }

        if (isTokenExpired(token)) {
            return new TokenValidationResponse(false, false);
        }

        Date expiration = claims.getExpiration();
        long remaining = expiration.getTime() - System.currentTimeMillis();
        long total = expiration.getTime() - claims.getIssuedAt().getTime();

        boolean shouldRefresh = ((remaining * 100) / total) <= 25;

        return new TokenValidationResponse(true, shouldRefresh);
    }

    // ⭐ MAIN: Refresh Token genereren
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
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    private boolean isRefreshTokenValid(String tokenHash) {
        Instant now = Instant.now();
        return refreshRepo.existsByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(tokenHash, now);
    }

    // ⭐ MAIN: Refresh token flow
    public RefreshToken refreshToken(String refreshTokenHash) {
        Optional<RefreshToken> oldTokenOpt = refreshRepo.findByTokenHash(refreshTokenHash);

        if (oldTokenOpt.isEmpty()) return null;
        if (!isRefreshTokenValid(refreshTokenHash)) return null;

        try {
            String newTokenHash = generateRefreshToken();
            Instant expiry = Instant.now().plusSeconds(15 * 60);

            RefreshToken old = oldTokenOpt.get();
            RefreshToken newToken = new RefreshToken(old.getUserId(), newTokenHash, expiry);

            old.setRevokedAt(Instant.now());
            newToken.setFamilyId(old.getFamilyId());

            refreshRepo.saveAllAndFlush(List.of(old, newToken));

            return newToken;
        } catch (Exception e) {
            log.error("Refresh token error", e);
            return null;
        }
    }
}
