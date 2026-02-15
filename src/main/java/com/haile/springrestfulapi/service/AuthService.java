package com.haile.springrestfulapi.service;

import com.haile.springrestfulapi.config.JwtService;
import com.haile.springrestfulapi.entity.RefreshTokenEntity;
import com.haile.springrestfulapi.entity.UserEntity;
import com.haile.springrestfulapi.entity.dto.request.LoginRequestDTO;
import com.haile.springrestfulapi.entity.dto.response.ExchangeTokenResponseDTO;
import com.haile.springrestfulapi.entity.dto.response.LoginResponseDTO;
import com.haile.springrestfulapi.helper.exception.ResourceNotFoundException;
import com.haile.springrestfulapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public LoginResponseDTO login(LoginRequestDTO dto) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword());

        Authentication authentication = authenticationManager.authenticate(authToken);

        UserEntity user = userService.findUserByEmail(authentication.getName());

        String token = jwtService.jwtCreateAccessToken(authentication, user.getId());

        String refreshToken = jwtService.createRefreshToken(user);

        String scope = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> !a.startsWith("FACTOR_"))
                .collect(Collectors.joining(" "));

        return LoginResponseDTO
                .builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .user(new LoginResponseDTO.OutputUser(user.getId(), authentication.getName(), scope))
                .build();
    }

    public ExchangeTokenResponseDTO exchangeToken(String token) {
        return jwtService.handleExchangeToken(token);
    }

    public LoginResponseDTO.OutputUser getAccount() {
        Authentication authentication = SecurityContextHolder.getContext()
                .getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userId = jwt.getClaimAsString("id");
        String email = jwt.getSubject();
        String role = jwt.getClaimAsString("role");

        LoginResponseDTO.OutputUser outputUser =
                LoginResponseDTO.OutputUser.builder()
                        .id(Long.parseLong(userId))
                        .email(email)
                        .role(role)
                        .build();
        return outputUser;
    }

    public void logout(String refreshToken) {
        RefreshTokenEntity token = refreshTokenService.getRefreshTokenByToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));
        refreshTokenService.deleteTokenById(token.getId());
    }
}
