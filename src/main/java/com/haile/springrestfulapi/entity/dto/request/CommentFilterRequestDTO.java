package com.haile.springrestfulapi.entity.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CommentFilterRequestDTO {
    private String content;

    private Long postId;
    
    private Long userId;
}
