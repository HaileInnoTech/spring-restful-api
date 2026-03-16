package com.haile.springrestfulapi.service;

import com.haile.springrestfulapi.config.JwtService;
import com.haile.springrestfulapi.entity.RefreshTokenEntity;
import com.haile.springrestfulapi.entity.RoleEntity;
import com.haile.springrestfulapi.entity.UserEntity;
import com.haile.springrestfulapi.entity.dto.request.LoginRequestDTO;
import com.haile.springrestfulapi.entity.dto.request.RegisterRequestDTO;
import com.haile.springrestfulapi.entity.dto.response.ExchangeTokenResponseDTO;
import com.haile.springrestfulapi.entity.dto.response.LoginResponseDTO;
import com.haile.springrestfulapi.helper.exception.ResourceNotFoundException;
import com.haile.springrestfulapi.repository.RoleRepository;
import com.haile.springrestfulapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    public LoginResponseDTO login(LoginRequestDTO dto) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(dto.getEmail(),
                                                                                                dto.getPassword());

        Authentication authentication = authenticationManager.authenticate(authToken);

        UserEntity user = userService.findUserByEmail(authentication.getName());

        String token = jwtService.jwtCreateAccessToken(authentication, user.getId());

        String refreshToken = jwtService.createRefreshToken(user);

        String scope = authentication.getAuthorities()
                                     .stream()
                                     .map(GrantedAuthority::getAuthority)
                                     .filter(a -> !a.startsWith("FACTOR_"))
                                     .collect(Collectors.joining(" "));

        return LoginResponseDTO.builder()
                               .accessToken(token)
                               .refreshToken(refreshToken)
                               .user(new LoginResponseDTO.OutputUser(user.getId(), authentication.getName(), scope))
                               .build();
    }

    public LoginResponseDTO oauthLogin(String email) {

        UserEntity user = userService.findUserByEmail(email);

        RoleEntity role = roleRepository.findById(user.getRole()
                                                      .getId())
                                        .orElseThrow(() -> new ResourceNotFoundException("KO tim2 dc role"));
        log.info("user: {}", user);
        log.info("role: {}", role);

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.getName()));

        Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, authorities);

        String accessToken = jwtService.jwtCreateAccessToken(authentication, user.getId());

        String refreshToken = jwtService.createRefreshToken(user);

        String scope = authorities.stream()
                                  .map(GrantedAuthority::getAuthority)
                                  .collect(Collectors.joining(" "));

        return LoginResponseDTO.builder()
                               .accessToken(accessToken)
                               .refreshToken(refreshToken)
                               .user(new LoginResponseDTO.OutputUser(user.getId(), email, scope))
                               .build();
    }

    ;

    public ExchangeTokenResponseDTO exchangeToken(String token) {
        return jwtService.handleExchangeToken(token);
    }

    public LoginResponseDTO.OutputUser getAccount() {
        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userId = jwt.getClaimAsString("id");
        String email = jwt.getSubject();
        String role = jwt.getClaimAsString("scope")
                         .replace("ROLE_", "");

        LoginResponseDTO.OutputUser outputUser = LoginResponseDTO.OutputUser.builder()
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

    public void createNewUser(RegisterRequestDTO user) {
        Boolean emailExists = userRepository.existsByEmail(user.getEmail());
        if (emailExists) {
            throw new ResourceNotFoundException("Email already exists. Please try again");
        }

        RoleEntity userRole = roleRepository.findAllByIdOrName(null, "user")
                                            .orElseThrow(() -> new ResourceNotFoundException(
                                                    "Role not found. Please try again"));

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        UserEntity newUser = UserEntity.builder()
                                       .email(user.getEmail())
                                       .password(hashedPassword)
                                       .username(user.getUsername())
                                       .address(user.getAddress())
                                       .role(userRole)
                                       .build();
        userRepository.save(newUser);
    }
}
