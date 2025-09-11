package com.example.authservice.domain.token;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
    RefreshToken save(RefreshToken token);
    Optional<RefreshToken> findActiveByHash(String tokenHash);
    void revoke(UUID id);
    void deleteById(UUID id);
}





