package com.haile.springrestfulapi.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDTO {
    @NotBlank(message = "Username không được để trống")
    private String username;

    @NotBlank(message = "Email không được để trống")
    private String email;

    private String address;
}
