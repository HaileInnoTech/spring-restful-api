package com.haile.springrestfulapi.entity.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserFilterRequestDTO {
    private String name;

    private String address;

    private String email;

    private String roleName;
}
