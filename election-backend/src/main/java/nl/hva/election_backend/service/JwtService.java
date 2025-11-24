package nl.hva.election_backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
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

    public String extractDisplayName(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        Claims claims = extractAllClaims(token);
        if (!claims.getIssuer().equals("ga-stemmen.nl")) return false;
        return !isTokenExpired(token);
    }
}
