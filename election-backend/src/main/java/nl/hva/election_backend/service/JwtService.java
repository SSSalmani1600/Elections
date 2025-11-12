package nl.hva.election_backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import nl.hva.election_backend.dto.TokenValidationResponse;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    private final String SECRET_KEY = System.getenv().getOrDefault("JWT_SECRET_B64",
            "dLRPokUNE7CfDTv2Nq1JmKZLuDSbMLvfTn9yJAxCx4A=");
    private final String issuer = "ga-stemmen.nl";


    public String generateToken(String displayName) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder().claims().add(claims).subject(displayName)
                .issuer(issuer).issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000))
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

    public String extractDisplayName(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public TokenValidationResponse validateToken(String token) {
        Claims claims = extractAllClaims(token);
        if (!claims.getIssuer().equals("ga-stemmen.nl")) {
            return new TokenValidationResponse(false, false);
        }
        if (isTokenExpired(token)) {
            return new TokenValidationResponse(false, true);
        };

        Date expirationDate = claims.getExpiration();
        long remainingTime = expirationDate.getTime() - (new Date().getTime());
        long totalTime =  expirationDate.getTime() - claims.getIssuedAt().getTime();

        if (remainingTime / totalTime <= 25) {
            return new TokenValidationResponse(true, true);
        }

        return new TokenValidationResponse(true, false);
    }
}
