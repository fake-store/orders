package xyz.fakestore.orders.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Map;

@Component
public class JwtUtil {

    private final SecretKey key;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public Map<String, Object> validateAndGetClaims(String token) {
        try {
            var payload = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return Map.of(
                "userId", payload.getSubject(),
                "email", payload.get("email"),
                "username", payload.get("username")
            );
        } catch (Exception e) {
            return null;
        }
    }
}
