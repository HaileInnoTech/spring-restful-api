package com.haile.springrestfulapi.service;

import com.haile.springrestfulapi.entity.RefreshTokenEntity;
import com.haile.springrestfulapi.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public void createNewRefreshToken(RefreshTokenEntity refreshTokenEntity) {
        this.refreshTokenRepository.save(refreshTokenEntity);
    }

    public Optional<RefreshTokenEntity> getRefreshTokenByToken(String token) {
        return this.refreshTokenRepository.findByToken(token);
    }

    public void deleteToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }

    public void deleteTokenById(Long id) {
        refreshTokenRepository.deleteById(id);
    }
}
