package com.haile.springrestfulapi.entity.dto.response;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleResponseDTO {
    private Long id;
    private String name;
    private String description;

}
