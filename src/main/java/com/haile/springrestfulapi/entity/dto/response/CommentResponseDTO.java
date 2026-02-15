package com.haile.springrestfulapi.entity.dto.response;

import lombok.*;

import java.time.Instant;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponseDTO {

    private Long id;

    private String content;

    private boolean isApproved = false;

    private Instant createdAt;

    private Instant updatedAt;

    private OutputUser user;
    private OutputPost post;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutputUser {
        private Long id;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutputPost {
        private Long id;
        private String title;
        private String content;
    }


}
