package com.haile.springrestfulapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.haile.springrestfulapi.entity.dto.response.LoginResponseDTO;
import com.haile.springrestfulapi.helper.ApiResponse;
import com.haile.springrestfulapi.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthService authService;

    @Value("${custom.jwt.access-token.validity-in-seconds}")
    private Long validityInSeconds;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");

        LoginResponseDTO loginResponseDTO = authService.oauthLogin(email);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", loginResponseDTO.getRefreshToken())
                                              .httpOnly(true)
                                              .secure(true)
                                              .path("/")
                                              .maxAge(validityInSeconds)
                                              .build();
        // need to fix
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.sendRedirect("http://localhost:3000/oauth-success?accesstoken=" + loginResponseDTO.getAccessToken());
    }
}