package com.haile.springrestfulapi.entity.dto.response;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;

    private String username;

    private String email;

    private String address;

    private RoleResponseDTO role;
}
