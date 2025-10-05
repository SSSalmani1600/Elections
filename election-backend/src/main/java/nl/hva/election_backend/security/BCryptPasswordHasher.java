package nl.hva.election_backend.security;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordHasher implements PasswordHasher {
    @Override
    public String hash(String raw) {
        return BCrypt.hashpw(raw, BCrypt.gensalt(12));
    }
    @Override
    public boolean matches(String raw, String hashed) {
        return BCrypt.checkpw(raw, hashed);
    }
}
