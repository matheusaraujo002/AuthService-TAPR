package com.example.authservice.infrastructure.persistence;

import com.example.authservice.domain.token.RefreshToken;
import com.example.authservice.domain.token.RefreshTokenRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaRefreshTokenRepository implements RefreshTokenRepository {
    private final RefreshTokenJpa jpa;

    public JpaRefreshTokenRepository(RefreshTokenJpa jpa) {
        this.jpa = jpa;
    }

    @Override
    public RefreshToken save(RefreshToken token) {
        return jpa.save(token);
    }

    @Override
    public Optional<RefreshToken> findActiveByHash(String tokenHash) {
        return jpa.findActiveByHash(tokenHash, Instant.now());
    }

    @Override
    public void revoke(UUID id) {
        jpa.revoke(id);
    }

    @Override
    public void deleteById(UUID id) {
        jpa.deleteById(id);
    }
}





