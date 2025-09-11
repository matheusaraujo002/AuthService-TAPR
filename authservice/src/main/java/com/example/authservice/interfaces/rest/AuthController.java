package com.example.authservice.interfaces.rest;

import com.example.authservice.application.auth.PasswordLoginHandler;
import com.example.authservice.interfaces.rest.dto.auth.PasswordLoginRequest;
import com.example.authservice.interfaces.rest.dto.auth.TokenResponse;
import com.example.authservice.interfaces.rest.dto.auth.RefreshRequest;
import com.example.authservice.application.token.RefreshTokenService;
import com.example.authservice.application.port.TokenService;
import com.example.authservice.domain.token.RefreshToken;
import com.example.authservice.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final PasswordLoginHandler passwordLoginHandler;
    private final RefreshTokenService refreshTokenService;
    private final TokenService tokenService;

    @PostMapping("/login/password")
    public ResponseEntity<TokenResponse> loginWithPassword(@Valid @RequestBody PasswordLoginRequest request) {
        TokenResponse response = passwordLoginHandler.handle(request.email(), request.password());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        RefreshToken token = refreshTokenService.validate(request.refreshToken())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        User user = token.getUser();
        refreshTokenService.revoke(token.getId());
        TokenService.TokenPair pair = tokenService.issue(user);
        return ResponseEntity.ok(new TokenResponse(pair.accessToken(), pair.refreshToken(), pair.expiresInSeconds()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshRequest request) {
        refreshTokenService.validate(request.refreshToken())
                .ifPresent(rt -> refreshTokenService.revoke(rt.getId()));
        return ResponseEntity.noContent().build();
    }
}
