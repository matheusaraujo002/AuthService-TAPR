package com.example.authservice.domain.auth.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Embeddable
@NoArgsConstructor
@Getter
public class ExpiresAt {

    @Column(name = "expires_at", nullable = false)
    private Instant value;

    public ExpiresAt(Instant value) {
        if (value == null) {
            throw new IllegalArgumentException("expiresAt obrigatório");
        }

        this.value = value;
    }

    public static ExpiresAt of(Instant value) {
        return new ExpiresAt(value);
    }

    public boolean isAfter(Instant value) {
        return this.value.isAfter(value);
    }
}
