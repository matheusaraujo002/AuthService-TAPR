package com.example.authservice.application.auth;

import com.example.authservice.application.port.TokenService;
import com.example.authservice.domain.auth.MagicLink;
import com.example.authservice.domain.auth.MagicLinkRepository;
import com.example.authservice.domain.user.User;
import com.example.authservice.domain.user.UserRepository;
import com.example.authservice.interfaces.rest.dto.auth.MagicLinkRequest;
import com.example.authservice.interfaces.rest.dto.auth.TokenResponse;
import com.example.authservice.support.Digests;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerifyMagicLinkHandler {
    private final MagicLinkRepository magicLinkRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public TokenResponse handle(String rawToken) {
        String hash = Digests.sha256Hex(rawToken);
        Instant now = Instant.now();

        Optional<MagicLink> linkOpt = magicLinkRepository.findValidByHash(hash, now);
        MagicLink link = linkOpt.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Link inválido ou expirado"));

        UUID userId = link.getUserId();
        User user = userRepository.findById(userId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado"));

        link.consume(now);
        magicLinkRepository.save(link);
        TokenService.TokenPair pair = tokenService.issue(user);

        return new TokenResponse(
            pair.accessToken(),
            pair.refreshToken(),
            pair.expiresInSeconds()
        );
    }
}
