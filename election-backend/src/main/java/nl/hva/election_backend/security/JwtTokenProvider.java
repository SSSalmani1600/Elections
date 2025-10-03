package nl.hva.election_backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenProvider {
    private final Key key;
    private final String issuer = "ga-stemmen.nl";

    public JwtTokenProvider(Key key) {
        String b64 = System.getenv().getOrDefault("JWT_SECRET_B64",
                "REPLACE_WITH_BASE64_SECRET_AT_LEAST_32_BYTES____________");
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(b64));
    }

    public String generateToken(String displayName) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder().claims().add(claims).subject(displayName)
                .issuer(issuer).issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 30))
                .and().signWith(this.getKey()).compact();
    }

    private Key getKey() {
        return this.key;
    }
}
