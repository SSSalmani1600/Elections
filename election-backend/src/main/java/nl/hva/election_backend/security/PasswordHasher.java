package nl.hva.election_backend.security;

public interface PasswordHasher {
    String hash(String raw);
    boolean matches(String raw, String hashed);
}
