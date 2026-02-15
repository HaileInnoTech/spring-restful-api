package com.haile.springrestfulapi.controller;

import com.haile.springrestfulapi.config.JwtService;
import com.haile.springrestfulapi.entity.dto.request.LoginRequestDTO;
import com.haile.springrestfulapi.entity.dto.response.ExchangeTokenResponseDTO;
import com.haile.springrestfulapi.entity.dto.response.LoginResponseDTO;
import com.haile.springrestfulapi.helper.ApiResponse;
import com.haile.springrestfulapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;


    @Value("${custom.jwt.refresh-token.validity-in-seconds}")
    Long validityInSeconds;
//    @GetMapping("/auth")
//    public ResponseEntity<ApiResponse<String>> createAuth(){
//        return  ApiResponse.created(jwtConfig.JwtCreateAccessToken());
//    }

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> createAuth(@Valid @RequestBody LoginRequestDTO dto) {
        LoginResponseDTO loginResponseDTO = authService.login(dto);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", loginResponseDTO.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(validityInSeconds)
                .build();
        ApiResponse<LoginResponseDTO> res = new ApiResponse<>(HttpStatus.OK, "", loginResponseDTO, "");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res);
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ApiResponse<ExchangeTokenResponseDTO>> refreshToken(@Valid @RequestParam("token") String refreshToken) {
        return ApiResponse.success((authService.exchangeToken(refreshToken)));
    }

    @PostMapping("/auth/refresh-with-cookie")
    public ResponseEntity<ApiResponse<ExchangeTokenResponseDTO>> refreshTokenWithCookie(@CookieValue(required = false) String refreshToken) {
        ExchangeTokenResponseDTO dto = authService.exchangeToken(refreshToken);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", dto.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(validityInSeconds)
                .build();

        ApiResponse<ExchangeTokenResponseDTO> res = new ApiResponse<>(HttpStatus.OK, "", dto, "");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(res);
    }

    @GetMapping("/auth/account")
    public ResponseEntity<ApiResponse<LoginResponseDTO.OutputUser>> getAccount() {
        return ApiResponse.success(authService.getAccount());
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<String> logout(@CookieValue(required = false) String refreshToken) {
        authService.logout(refreshToken);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("ok");
    }
}
