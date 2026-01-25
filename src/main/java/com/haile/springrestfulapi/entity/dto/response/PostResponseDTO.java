package com.haile.springrestfulapi.entity.dto.response;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDTO {
    private Long id;

    private String title;

    private String content;

    private List<OutputTag> tags;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OutputTag {
        private Long id;

        private String name;
    }
}

