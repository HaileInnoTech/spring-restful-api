package com.haile.springrestfulapi.entity.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequestDTO {
    @NotBlank(message = "title không được để trống")
    private String title;

    @NotBlank(message = "content không được để trống")
    private String content;

    @NotNull(message = "tags không được để trống")
    @Valid
    private List<InputTag> tags;

    @NotNull(message = "user không được để trống")
    @Valid
    private InputUser user;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InputTag {

        @NotNull(message = "tag.id không được để trống")
        private Long id;

        @NotBlank(message = "tag.name không được để trống")
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InputUser {
        @NotNull(message = "user.id không được để trống")
        private Long id;
    }
}

