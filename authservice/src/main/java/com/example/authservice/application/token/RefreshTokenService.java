package com.example.authservice.application.token;

import com.example.authservice.application.port.TokenService;
import com.example.authservice.domain.token.RefreshToken;
import com.example.authservice.domain.token.RefreshTokenRepository;
import com.example.authservice.domain.user.User;
import com.example.authservice.infrastructure.config.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository repository;
    private final JwtProperties props;

    public record RotationResult(String refreshToken, Instant expiresAt, UUID id) {}

    public RotationResult issue(User user) {
        Instant expiresAt = Instant.now().plusSeconds(props.getRefreshTtlSeconds());
        String raw = UUID.randomUUID().toString() + ":" + user.getId() + ":" + expiresAt.toEpochMilli();
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        String hash = sha256(token);

        RefreshToken entity = new RefreshToken();
        entity.setUser(user);
        entity.setTokenHash(hash);
        entity.setExpiresAt(expiresAt);
        entity = repository.save(entity);

        return new RotationResult(token, expiresAt, entity.getId());
    }

    public Optional<RefreshToken> validate(String presentedToken) {
        String hash = sha256(presentedToken);
        return repository.findActiveByHash(hash);
    }

    @Transactional
    public void revoke(UUID id) {
        repository.revoke(id);
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] out = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(out);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 não disponível.", e);
        }
    }
}





