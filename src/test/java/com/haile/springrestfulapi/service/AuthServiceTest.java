package com.haile.springrestfulapi.service;

import com.haile.springrestfulapi.config.JwtService;
import com.haile.springrestfulapi.entity.RoleEntity;
import com.haile.springrestfulapi.entity.UserEntity;
import com.haile.springrestfulapi.entity.dto.request.LoginRequestDTO;
import com.haile.springrestfulapi.entity.dto.request.RegisterRequestDTO;
import com.haile.springrestfulapi.entity.dto.response.LoginResponseDTO;
import com.haile.springrestfulapi.helper.exception.ResourceNotFoundException;
import com.haile.springrestfulapi.repository.RoleRepository;
import com.haile.springrestfulapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock AuthenticationManager authenticationManager;
    @Mock JwtService jwtService;
    @Mock UserRepository userRepository;
    @Mock UserService userService;
    @Mock RefreshTokenService refreshTokenService;
    @Mock PasswordEncoder passwordEncoder;
    @Mock RoleRepository roleRepository;
    @Mock RoleService roleService;

    @InjectMocks
    AuthService authService;

    private UserEntity mockUser;
    private RoleEntity mockRole;

    @BeforeEach
    void setUp() {
        mockRole = new RoleEntity();
        mockRole.setId(1L);
        mockRole.setName("user");

        mockUser = UserEntity.builder()
                .id(1L)
                .email("test@example.com")
                .password("hashedPassword")
                .username("testuser")
                .role(mockRole)
                .build();
    }

    // ─── login() ────────────────────────────────────────────────────────────

    @Test
    void login_withValidCredentials_returnsLoginResponse() {
        LoginRequestDTO dto = new LoginRequestDTO("test@example.com", "password123");

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test@example.com");
        when(auth.getAuthorities()).thenReturn(List.of());
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(userService.findUserByEmail("test@example.com")).thenReturn(mockUser);
        when(jwtService.jwtCreateAccessToken(any(), any())).thenReturn("access-token");
        when(jwtService.createRefreshToken(any())).thenReturn("refresh-token");

        LoginResponseDTO result = authService.login(dto);

        assertThat(result.getAccessToken()).isEqualTo("access-token");
        assertThat(result.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(result.getUser().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void login_withInvalidCredentials_throwsBadCredentialsException() {
        LoginRequestDTO dto = new LoginRequestDTO("wrong@example.com", "wrongpass");
        when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(dto))
                .isInstanceOf(BadCredentialsException.class);
    }

    // ─── createNewUser() ────────────────────────────────────────────────────

    @Test
    void createNewUser_withNewEmail_savesUser() {
        RegisterRequestDTO dto = new RegisterRequestDTO("newuser", "new@example.com", "pass123", "Hanoi");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(roleRepository.findAllByIdOrName(null, "user")).thenReturn(Optional.of(mockRole));
        when(passwordEncoder.encode("pass123")).thenReturn("hashed");

        authService.createNewUser(dto);

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void createNewUser_withDuplicateEmail_throwsResourceNotFoundException() {
        RegisterRequestDTO dto = new RegisterRequestDTO("user", "test@example.com", "pass", "Hanoi");
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.createNewUser(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    void createNewUser_whenRoleNotFound_throwsResourceNotFoundException() {
        RegisterRequestDTO dto = new RegisterRequestDTO("user", "new@example.com", "pass", "Hanoi");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(roleRepository.findAllByIdOrName(null, "user")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.createNewUser(dto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Role not found");
    }
}
