package com.haile.springrestfulapi.config;

import com.haile.springrestfulapi.entity.RefreshTokenEntity;
import com.haile.springrestfulapi.entity.UserEntity;
import com.haile.springrestfulapi.entity.dto.response.ExchangeTokenResponseDTO;
import com.haile.springrestfulapi.helper.exception.ResourceNotFoundException;
import com.haile.springrestfulapi.service.RefreshTokenService;
import com.haile.springrestfulapi.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS256;

    private final JwtEncoder jwtEncoder;
    private final UserService userService;

    @Value("${custom.jwt.access-token.validity-in-seconds}")
    private Long accessTokenExpiration;

    @Value("${custom.jwt.refresh-token.validity-in-seconds}")
    private Long refreshTokenExpiration;

    private final RefreshTokenService refreshTokenService;

    public String jwtCreateAccessToken(Authentication authentication, Long userId) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

        // ghép các quyền thành 1 string: "ROLE_USER ROLE_ADMIN"
        String scope = authentication.getAuthorities()
                                     .stream()
                                     .map(GrantedAuthority::getAuthority)
                                     .filter(a -> !a.startsWith("FACTOR_"))
                                     .collect(Collectors.joining(" "));


        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(authentication.getName())
                .claim("scope", scope)
                .claim("id", userId)
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();

    }

    public String generateSecureToken() {
        byte[] randomBytes = new byte[64]; // 512 bits
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public String createRefreshToken(UserEntity user) {
        Instant now = Instant.now();
        Instant validity = now.plus(this.refreshTokenExpiration, ChronoUnit.SECONDS);
        String  newToken = generateSecureToken();
        RefreshTokenEntity.builder().token(newToken).expiredAt(validity).user(user).build();
        refreshTokenService.createNewRefreshToken(RefreshTokenEntity.builder().token(newToken).user(user).expiredAt(validity).build());
        return newToken;
    }

    @Transactional
    public ExchangeTokenResponseDTO handleExchangeToken(String token) {

        RefreshTokenEntity  tokenEntity = this.refreshTokenService.getRefreshTokenByToken(token)
                        .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));

        Instant now = Instant.now();

        boolean isValid = now.isBefore(tokenEntity.getExpiredAt());
        if (!isValid) {
            throw new ResourceNotFoundException("Refresh token not found");
        }

        UserEntity user = userService.getUserById(tokenEntity.getUser().getId());

        Instant validity = now.plus(this.accessTokenExpiration, ChronoUnit.SECONDS);

        // ghép các quyền thành 1 string: "ROLE_USER ROLE_ADMIN"
        String scope = "ROEL_" +user.getRole().getName();

        // @formatter:off
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuedAt(now)
                .expiresAt(validity)
                .subject(user.getEmail())
                .claim("scope", scope)
                .claim("id", user.getId())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        String accessToken =  this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();


        String refreshToken = createRefreshToken(user);

        refreshTokenService.deleteToken(token);


        return ExchangeTokenResponseDTO
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(new ExchangeTokenResponseDTO.OutputUser(user.getId(),user.getEmail(), user.getRole().getName()))
                .build();
    }
}
