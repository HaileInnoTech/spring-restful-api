package com.haile.springrestfulapi.entity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDTO {

    @NotBlank(message = "Email or Pass can not be blank")
    private String email;

    @NotBlank(message = "Email or Pass can not be blank")
    private String password;

}
