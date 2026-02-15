package com.haile.springrestfulapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshTokenEntity extends BaseEntity {

    @NotNull(message = "Token must not be null")
    private String token;

    private Instant expiredAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

}
