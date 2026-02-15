package com.haile.springrestfulapi.entity.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ExchangeTokenResponseDTO {
    private String accessToken;
    private String tokenType = "Bearer";
    private String refreshToken;
    private OutputUser user;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OutputUser {
        private Long id;
        private String email;
        private String role;
    }

}
