package com.example.authservice.infrastructure.persistence;

import com.example.authservice.domain.token.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpa extends JpaRepository<RefreshToken, UUID> {

    @Query("select rt from RefreshToken rt where rt.tokenHash = :hash and rt.revoked = false and rt.expiresAt > :now")
    Optional<RefreshToken> findActiveByHash(@Param("hash") String hash, @Param("now") Instant now);

    @Modifying
    @Query("update RefreshToken rt set rt.revoked = true where rt.id = :id")
    void revoke(@Param("id") UUID id);
}





