package com.haile.springrestfulapi.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@AllArgsConstructor
@Setter
@Getter
@ToString
@NoArgsConstructor
public class RegisterRequestDTO {

    @NotBlank(message = "username không được để trống")
    private String username;

    @NotBlank(message = "email không được để trống")
    private String email;

    @NotBlank(message = "password không được để trống")
    private String password;

    @NotBlank(message = "address không được để trống")
    private String address;
}
