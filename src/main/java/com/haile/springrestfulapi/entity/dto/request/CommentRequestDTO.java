package com.haile.springrestfulapi.entity.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequestDTO {
    @NotBlank(message = "Content không được để trống")
    private String content;

    private boolean isApproved = false;

    @NotNull
    @Valid
    private InputUser user;

    @NotNull
    @Valid
    private InputPost post;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InputUser {
        @NotNull(message = "user.id không được để trống")
        private Long id;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InputPost {
        @NotNull(message = "post.id không được để trống")
        private Long id;
    }
}
