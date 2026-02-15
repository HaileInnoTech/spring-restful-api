package com.haile.springrestfulapi.config;

import com.haile.springrestfulapi.helper.exception.CustomAccessDeniedHandler;
import com.haile.springrestfulapi.helper.exception.CustomAuthenticationEntryPoint;
import com.haile.springrestfulapi.service.UserService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    //gán biến trong properties.yaml
    @Value("${custom.jwt.access-token.base64-secret}")
    String jwtKey;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(getSecretKey())
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }


    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter scopeConverter = new JwtGrantedAuthoritiesConverter();
        scopeConverter.setAuthoritiesClaimName("scope");
        scopeConverter.setAuthorityPrefix(""); // giữ nguyên, không thêm "SCOPE_"

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(scopeConverter);
        return converter;
    }

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey)
                .decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, JwtService.JWT_ALGORITHM.getName());
    }


    // ghi đè bean userDetailsService bằng CustomUserDetailsService
    @Bean
    UserDetailsService userDetailsService(UserService userService) {
        return new CustomUserDetailsService(userService);
    }

    // ghi đè bean daoAuthenticationProvider, và hàm so sánh password bằng passwordEncoder
    @Bean
    AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider dao = new DaoAuthenticationProvider(userDetailsService);
        dao.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(dao);
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http,
                                                   CustomAccessDeniedHandler customAccessDeniedHandler,
                                                   CustomAuthenticationEntryPoint customAuthenticationEntryPoint) throws Exception {

        http.csrf(c -> c.disable());

        http.formLogin(form -> form.disable());

        http.sessionManagement(s -> s.disable());


        http.authorizeHttpRequests(
                requests -> requests.requestMatchers("/auth/login", "/auth/refresh", "/auth/refresh-with-cookie")
                        .permitAll()
                        .requestMatchers("/users/**")
                        .hasRole("user")
                        .anyRequest()
                        .authenticated()

        );

        http.oauth2ResourceServer(oauth2 -> oauth2
                .accessDeniedHandler(customAccessDeniedHandler)
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));


        return http.build();
    }


}
